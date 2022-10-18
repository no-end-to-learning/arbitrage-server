package me.qiujun.arbitrage.task.gate;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.gate.GateSpotHttpClient;
import me.qiujun.arbitrage.adapter.gate.GateSpotWebSocketClient;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateOrderEvent;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotOrderResponse;
import me.qiujun.arbitrage.adapter.gate.exception.GateException;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.bean.event.CurrentConfigsChangeEvent;
import me.qiujun.arbitrage.bean.event.OrderTradeEvent;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
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
        value = "app.gate.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class GateSpotOrderWatchTask {

    @Autowired
    private GateSpotHttpClient gateSpotHttpClient;

    @Autowired
    private GateSpotWebSocketClient gateSpotWebSocketClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ApplicationEventPublisher publisher;

    private final ExecutorService executor = ExecutorUtil.newNamedCachedThreadPool("gate-watch-");

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
                .filter(item -> ExchangeEnum.GATE == item.getTradeExchange())
                .filter(item -> ExchangeMarketEnum.SPOT == item.getTradeExchangeMarket())
                .toList();
        if (configs.isEmpty()) {
            return;
        }

        // 监听订单成交事件
        connection = gateSpotWebSocketClient.onOrderEvent((ordersEvent) -> {
            List<GateOrderEvent> gateOrders = ordersEvent.getResult();
            for (GateOrderEvent gateOrder : gateOrders) {
                if (!gateOrder.getTransformedStatus().isFinal()) {
                    continue;
                }

                executor.submit(withTraceIdWrapper(() -> execute(gateOrder)));
            }
        });
    }


    @Scheduled(fixedRate = 60 * 1000)
    public void execute() throws InterruptedException {
        List<Order> orders = orderService.listUnfinished(ExchangeEnum.GATE, ExchangeMarketEnum.SPOT);
        if (orders.isEmpty()) {
            return;
        }

        List<Callable<Boolean>> callables = new ArrayList<>();
        for (Order order : orders) {
            callables.add(withMDCWrapper(() -> execute(order)));
        }
        executor.invokeAll(callables);
    }

    public boolean execute(GateOrderEvent gateOrder) {
        Order order = orderService.getByExchangeOrderId(gateOrder.getId());
        if (order == null) {
            log.debug("order {} not found", gateOrder.getId());
            return false;
        }

        log.info("order {} finished", order.getId());
        return execute(order, gateOrder.transformTo(order));
    }

    public boolean execute(Order order) {
        GateSpotOrderResponse orderResponse = gateSpotHttpClient.getOrder(order.getExchangeOrderId(), order.getSymbol());
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
            } catch (GateException e) {
                log.warn(e.getMessage());
                return false;
            } catch (Exception e) {
                log.error("gate spot order watch error", e);
                return false;
            }
        };
    }

}
