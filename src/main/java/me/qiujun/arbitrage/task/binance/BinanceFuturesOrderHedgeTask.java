package me.qiujun.arbitrage.task.binance;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesHttpClient;
import me.qiujun.arbitrage.adapter.binance.bean.request.BinanceOrderCreateRequest;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceFuturesOrderResponse;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.bean.base.SymbolConfig;
import me.qiujun.arbitrage.bean.event.OrderTradeEvent;
import me.qiujun.arbitrage.bean.order.OrderPlaceParams;
import me.qiujun.arbitrage.config.snowflake.Snowflake;
import me.qiujun.arbitrage.enums.*;
import me.qiujun.arbitrage.service.ConfigService;
import me.qiujun.arbitrage.service.MarketPriceService;
import me.qiujun.arbitrage.service.MarketSymbolService;
import me.qiujun.arbitrage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.binance.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BinanceFuturesOrderHedgeTask {

    @Autowired
    private BinanceFuturesHttpClient binanceFuturesHttpClient;

    @Autowired
    private ConfigService configService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Autowired
    private MarketSymbolService marketSymbolService;

    @Autowired
    private Snowflake snowflake;

    @Async
    @EventListener
    public synchronized void onOrderTradeEvent(OrderTradeEvent event) {
        Order order = event.getOrder();

        Config config = configService.getById(order.getConfigId());
        if (ExchangeEnum.BINANCE != config.getHedgeExchange()) {
            return;
        } else if (ExchangeMarketEnum.FUTURES != config.getHedgeExchangeMarket()) {
            return;
        }

        List<Order> orders = orderService.listNeedToHedge(config.getId());
        if (!orders.isEmpty()) {
            hedge(config, orders);
        }
    }

    private void hedge(Config config, List<Order> orders) {
        // 计算对冲数量
        BigDecimal needHedgeAmount = BigDecimal.ZERO;
        List<Long> needHedgeOrderIds = new ArrayList<>();
        for (Order order : orders) {
            needHedgeOrderIds.add(order.getId());
            if (order.getSide() == OrderSideEnum.BUY) {
                needHedgeAmount = needHedgeAmount.subtract(order.getBaseAmountFilled());
            } else {
                needHedgeAmount = needHedgeAmount.add(order.getBaseAmountFilled());
            }
        }

        // 对冲缩放
        SymbolConfig symbolConfig = marketSymbolService.getConfigFromHedgeConfig(config);
        needHedgeAmount = needHedgeAmount.divide(config.getHedgeScale(), symbolConfig.getAmountPrecision(), RoundingMode.HALF_EVEN);

        // 判断最小下单数量
        if (needHedgeAmount.abs().compareTo(symbolConfig.getMinOrderAmount()) < 0) {
            return;
        }

        // 对冲下单
        OrderPlaceParams orderPlaceParams = OrderPlaceParams.builder()
                .symbol(symbolConfig.getSymbol())
                .type(OrderTypeEnum.MARKET)
                .side(needHedgeAmount.compareTo(BigDecimal.ZERO) > 0 ? OrderSideEnum.BUY : OrderSideEnum.SELL)
                .amount(needHedgeAmount.abs())
                .build();
        BinanceOrderCreateRequest orderRequest = BinanceOrderCreateRequest.transformFrom(orderPlaceParams);
        log.info("start place hedge order, params {}", JSON.toJSONString(orderRequest));
        BinanceFuturesOrderResponse orderResponse = binanceFuturesHttpClient.createOrder(orderRequest);
        log.info("hedge order placed, exchange order id {}", orderResponse.getOrderId());

        // 查询汇率
        BigDecimal exchangeRate = marketPriceService.getExchangeRate(config.getTradeSymbol(), config.getHedgeSymbol());

        // 更新数据库
        Order order = orderResponse.transformTo(symbolConfig.getSymbol());
        order.setId(snowflake.genId());
        order.setConfigId(config.getId());
        order.setTradeType(OrderTradeTypeEnum.HEDGE);
        order.setExchangeRate(exchangeRate);
        orderService.create(order);
        orderService.updateHedged(needHedgeOrderIds, order.getId());
    }

}