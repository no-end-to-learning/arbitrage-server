package me.qiujun.arbitrage.task.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesHttpClient;
import me.qiujun.arbitrage.adapter.binance.bean.request.BinanceTradeFilterRequest;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceTradeResponse;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.enums.OrderSideEnum;
import me.qiujun.arbitrage.service.ConfigService;
import me.qiujun.arbitrage.service.MarketPriceService;
import me.qiujun.arbitrage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.binance.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BinanceFuturesOrderStatTask {

    @Autowired
    private BinanceFuturesHttpClient binanceFuturesHttpClient;

    @Autowired
    private ConfigService configService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Scheduled(fixedRate = 3 * 1000, initialDelay = 10 * 1000)
    public void execute() {
        List<Order> orders = orderService.listNeedToStat(ExchangeEnum.BINANCE, ExchangeMarketEnum.FUTURES);
        if (orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {
            execute(order);
        }
    }

    public void execute(Order order) {
        Order feeCurrencyAndAmount = getFeeCurrencyAndAmount(order);
        if (feeCurrencyAndAmount == null) {
            return;
        }

        BigDecimal quotePrice = marketPriceService.getValuationPrice(feeCurrencyAndAmount.getFeeCurrency());
        BigDecimal statCost = feeCurrencyAndAmount.getFeeAmount().multiply(quotePrice);
        BigDecimal statIncome = calStatIncome(order);

        order.setFeeCurrency(feeCurrencyAndAmount.getFeeCurrency());
        order.setFeeAmount(feeCurrencyAndAmount.getFeeAmount());
        order.setStatCost(statCost);
        order.setStatIncome(statIncome);

        orderService.updateWithPessimisticLock(order);
    }

    private Order getFeeCurrencyAndAmount(Order order) {
        BinanceTradeFilterRequest tradeFilterRequest = BinanceTradeFilterRequest.builder()
                .symbol(String.format("%s%s", order.getBaseCurrency(), order.getQuoteCurrency()))
                .endTime(order.getFinishedAt().getTime() + 300000L)
                .build();

        String feeCurrency = null;
        BigDecimal feeAmount = BigDecimal.ZERO;
        List<BinanceTradeResponse> trades = binanceFuturesHttpClient.listTrades(tradeFilterRequest);
        for (BinanceTradeResponse trade : trades) {
            if (!trade.getOrderId().toString().equals(order.getExchangeOrderId())) {
                continue;
            }

            feeCurrency = trade.getCommissionAsset();
            feeAmount = feeAmount.add(trade.getCommission().abs());
        }

        if (feeCurrency == null) {
            return null;
        } else {
            return Order.builder()
                    .feeCurrency(feeCurrency)
                    .feeAmount(feeAmount)
                    .build();
        }
    }

    private BigDecimal calStatIncome(Order order) {
        Config config = configService.getById(order.getConfigId());

        // 按交易成交量、成交额净值
        BigDecimal tradeBaseAmount = BigDecimal.ZERO;
        BigDecimal tradeHedgeQuoteAmount = BigDecimal.ZERO;
        List<Order> tradeOrders = orderService.listByHedgeId(order.getId());
        for (Order tradeOrder : tradeOrders) {
            BigDecimal orderTradeHedgeQuoteAmount = tradeOrder.getQuoteAmountFilled()
                    .divide(tradeOrder.getExchangeRate(), 18, RoundingMode.HALF_EVEN);

            if (tradeOrder.getSide() == OrderSideEnum.BUY) {
                tradeBaseAmount = tradeBaseAmount.add(tradeOrder.getBaseAmountFilled());
                tradeHedgeQuoteAmount = tradeHedgeQuoteAmount.add(orderTradeHedgeQuoteAmount);
            } else {
                tradeBaseAmount = tradeBaseAmount.subtract(tradeOrder.getBaseAmountFilled());
                tradeHedgeQuoteAmount = tradeHedgeQuoteAmount.subtract(orderTradeHedgeQuoteAmount);
            }
        }
        tradeBaseAmount = tradeBaseAmount.abs();
        tradeHedgeQuoteAmount = tradeHedgeQuoteAmount.abs();

        // 双方成交价格（统一使用对冲币对）
        BigDecimal tradePrice = tradeHedgeQuoteAmount.divide(tradeBaseAmount, RoundingMode.HALF_EVEN);
        BigDecimal hedgePrice = order.getQuoteAmountFilled()
                .divide(config.getHedgeScale(), 18, RoundingMode.HALF_EVEN)
                .divide(order.getBaseAmountFilled(), 18, RoundingMode.HALF_EVEN);

        // 按对冲报价币的收入
        BigDecimal incomeAmountInQuoteCurrency;
        if (order.getSide() == OrderSideEnum.BUY) {
            incomeAmountInQuoteCurrency = tradePrice.subtract(hedgePrice).multiply(tradeBaseAmount);
        } else {
            incomeAmountInQuoteCurrency = hedgePrice.subtract(tradePrice).multiply(tradeBaseAmount);
        }

        // USDT 计价的收入
        BigDecimal quotePrice = marketPriceService.getValuationPrice(order.getQuoteCurrency());
        return incomeAmountInQuoteCurrency.multiply(quotePrice);
    }
}
