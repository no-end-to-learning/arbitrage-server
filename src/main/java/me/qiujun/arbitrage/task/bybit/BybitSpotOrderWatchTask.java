package me.qiujun.arbitrage.task.bybit;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.bybit.BybitSpotHttpClient;
import me.qiujun.arbitrage.adapter.bybit.BybitSpotWebSocketClient;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitOrderEvent;
import me.qiujun.arbitrage.adapter.bybit.bean.response.BybitOrderResponse;
import me.qiujun.arbitrage.adapter.bybit.exception.BybitException;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.bean.event.CurrentConfigsChangeEvent;
import me.qiujun.arbitrage.bean.event.OrderTradeEvent;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.MarketPriceService;
import me.qiujun.arbitrage.service.OrderService;
import me.qiujun.arbitrage.util.ExecutorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.bybit.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BybitSpotOrderWatchTask {

    @Autowired
    private BybitSpotHttpClient bybitSpotHttpClient;

    @Autowired
    private BybitSpotWebSocketClient bybitSpotWebSocketClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Autowired
    private ApplicationEventPublisher publisher;

    private final ExecutorService executor = ExecutorUtil.newNamedCachedThreadPool("bybit-watch-");

    private Closeable connection = null;

    @Async
    @EventListener
    public void onCurrentConfigsChangeEvent(CurrentConfigsChangeEvent event) {
        // 连接已建立则关闭连接
        if (connection != null) {
            try {
                connection.close();
                log.info("connection manual closed due to configs change");
            } catch (IOException e) {
                log.error("connection manual close error", e);
            }
        }

        // 如果没有相关币对则不建立 WebSocket 连接
        List<Config> configs = event.getNewConfigs().stream()
                .filter(item -> ExchangeEnum.BYBIT == item.getTradeExchange())
                .filter(item -> ExchangeMarketEnum.SPOT == item.getTradeExchangeMarket())
                .toList();
        if (configs.isEmpty()) {
            return;
        }

        // 监听订单成交事件
        connection = bybitSpotWebSocketClient.onOrderEvent((ordersEvent) -> {
            List<BybitOrderEvent> bybitOrders = ordersEvent.getData();
            for (BybitOrderEvent bybitOrder : bybitOrders) {
                if (!bybitOrder.getTransformedStatus().isFinal()) {
                    continue;
                }

                executor.submit(withTraceIdWrapper(() -> execute(bybitOrder)));
            }
        });
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void execute() throws InterruptedException {
        List<Order> orders = orderService.listUnfinished(ExchangeEnum.BYBIT, ExchangeMarketEnum.SPOT);
        if (orders.isEmpty()) {
            return;
        }

        List<Callable<Boolean>> callables = new ArrayList<>();
        for (Order order : orders) {
            callables.add(withMDCWrapper(() -> execute(order)));
        }
        executor.invokeAll(callables);
    }

    public boolean execute(BybitOrderEvent bybitOrder) {
        Order order = orderService.getByExchangeOrderId(bybitOrder.getOrderId());
        if (order == null) {
            log.debug("order {} not found", bybitOrder.getOrderId());
            return false;
        }

        log.info("order {} finished", order.getId());
        return execute(order, bybitOrder.transformTo(order));
    }

    public boolean execute(Order order) {
        BybitOrderResponse orderResponse = bybitSpotHttpClient.getOrder(order.getExchangeOrderId());
        return execute(order, orderResponse.transformTo(order));
    }

    public boolean execute(Order oldOrder, Order newOrder) {
        if (oldOrder.getStatus().isFinal() && !newOrder.getStatus().isFinal()) {
            return false;
        }

        if (!orderService.updateWithPessimisticLock(newOrder)) {
            return false;
        }

        if (newOrder.getStatus().isFinal() && newOrder.getStatus().hasFilled()) {
            publisher.publishEvent(new OrderTradeEvent(this, newOrder));
        }

        return true;
    }

    private Callable<Boolean> withMDCWrapper(Callable<Boolean> callable) {
        return ExecutorUtil.withMDC(wrapper(callable));
    }

    private Callable<Boolean> withTraceIdWrapper(Callable<Boolean> callable) {
        return ExecutorUtil.withTraceId(wrapper(callable));
    }

    private Callable<Boolean> wrapper(Callable<Boolean> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (BybitException e) {
                log.warn(e.getMessage());
                return false;
            } catch (Exception e) {
                log.error(null, e);
                return false;
            }
        };
    }

}
