package me.qiujun.arbitrage.task.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesHttpClient;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.MarketPriceService;
import me.qiujun.arbitrage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.binance.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BinanceFuturesOrderWatchTask {

    @Autowired
    private BinanceFuturesHttpClient binanceFuturesHttpClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Scheduled(fixedRate = 1000)
    public void execute() {
        List<Order> orders = orderService.listUnfinished(ExchangeEnum.BINANCE, ExchangeMarketEnum.FUTURES);
        if (orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {
            String symbol = String.format("%s%s", order.getBaseCurrency(), order.getQuoteCurrency());
            Order newestOrder = binanceFuturesHttpClient.getOrder(symbol, order.getExchangeOrderId()).transformTo(order);
            if (order.getStatus().isFinal() && !newestOrder.getStatus().isFinal()) {
                continue;
            }

            orderService.updateWithPessimisticLock(newestOrder);
        }
    }

}
