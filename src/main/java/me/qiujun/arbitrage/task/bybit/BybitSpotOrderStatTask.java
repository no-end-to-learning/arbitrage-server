package me.qiujun.arbitrage.task.bybit;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.bybit.BybitSpotHttpClient;
import me.qiujun.arbitrage.adapter.bybit.bean.response.BybitTradeResponse;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.enums.OrderSideEnum;
import me.qiujun.arbitrage.service.MarketPriceService;
import me.qiujun.arbitrage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.bybit.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BybitSpotOrderStatTask {

    @Autowired
    private BybitSpotHttpClient bybitSpotHttpClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Scheduled(fixedRate = 3 * 1000, initialDelay = 10 * 1000)
    public void execute() throws InterruptedException {
        List<Order> orders = orderService.listNeedToStat(ExchangeEnum.BYBIT, ExchangeMarketEnum.SPOT);
        if (orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {
            execute(order);
        }
    }

    private void execute(Order order) {
        if (order.getSide() == OrderSideEnum.BUY) {
            order.setFeeCurrency(order.getBaseCurrency());
            order.setFeeAmount(BigDecimal.ZERO);
        } else {
            order.setFeeCurrency(order.getQuoteCurrency());
            order.setFeeAmount(BigDecimal.ZERO);
        }

        if (order.getStatus().hasFilled()) {
            Order feeCurrencyAndAmount = getFeeCurrencyAndAmount(order);
            order.setFeeCurrency(feeCurrencyAndAmount.getFeeCurrency());
            order.setFeeAmount(feeCurrencyAndAmount.getFeeAmount());
        }

        BigDecimal quotePrice = marketPriceService.getValuationPrice(order.getQuoteCurrency());
        if (order.getFeeCurrency().equals(order.getBaseCurrency())) {
            order.setStatCost(order.getFeeAmount().multiply(order.getPrice()).multiply(quotePrice));
        } else {
            order.setStatCost(order.getFeeAmount().multiply(quotePrice));
        }

        order.setStatIncome(BigDecimal.ZERO);

        orderService.updateWithPessimisticLock(order);
    }

    private Order getFeeCurrencyAndAmount(Order order) {
        String feeCurrency = order.getFeeCurrency();
        BigDecimal feeAmount = BigDecimal.ZERO;
        List<BybitTradeResponse.Item> trades = bybitSpotHttpClient.listTrades(order.getExchangeOrderId());
        for (BybitTradeResponse.Item trade : trades) {
            feeCurrency = trade.getFeeTokenId();
            feeAmount = feeAmount.add(trade.getExecFee());
        }

        return Order.builder()
                .feeCurrency(feeCurrency)
                .feeAmount(feeAmount)
                .build();
    }
}
