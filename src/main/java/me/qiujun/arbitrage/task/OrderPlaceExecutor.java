package me.qiujun.arbitrage.task;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.event.CurrentConfigChangeEvent;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.util.ExecutorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OrderPlaceExecutor {

    @Autowired
    private ApplicationContext ctx;

    private final Map<Long, OrderPlaceRunner> taskMap = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final Table<ExchangeEnum, ExchangeMarketEnum, OrderPlaceAbstract> orderPlaceHandlerTable = HashBasedTable.create();

    @PostConstruct
    private void init() {
        Map<String, Object> beansWithAnnotationMap = ctx.getBeansWithAnnotation(OrderPlaceAnnotation.class);
        for (Object beansWithAnnotation : beansWithAnnotationMap.values()) {
            if (!(beansWithAnnotation instanceof OrderPlaceAbstract orderTradeService)) {
                continue;
            }

            OrderPlaceAnnotation orderTrade = beansWithAnnotation.getClass().getAnnotation(OrderPlaceAnnotation.class);
            orderPlaceHandlerTable.put(orderTrade.exchange(), orderTrade.exchangeMarket(), orderTradeService);
        }
    }

    @PreDestroy
    public synchronized void onDestroy() throws Exception {
        log.info("start destroying order place executor");

        for (OrderPlaceRunner runner : taskMap.values()) {
            runner.stop();
        }

        executor.shutdown();
        while (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
            log.info("waiting for order place thread pool to shutdown");
        }

        log.info("order place executor is destroyed");
    }

    @Async
    @EventListener
    public synchronized void onCurrentConfigChangeEvent(CurrentConfigChangeEvent event) {
        // 停止旧任务
        stop(event.getOldConfig());

        // 启动新任务
        start(event.getNewConfig());
    }

    private void stop(Config config) {
        if (config == null || !taskMap.containsKey(config.getId())) {
            return;
        }

        taskMap.get(config.getId()).stop();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        taskMap.remove(config.getId());
    }

    private void start(Config config) {
        if (config == null || taskMap.containsKey(config.getId())) {
            return;
        }

        OrderPlaceAbstract orderPlaceService = orderPlaceHandlerTable.get(config.getTradeExchange(), config.getTradeExchangeMarket());
        if (orderPlaceService == null) {
            log.error("{} {} order place service not found", config.getTradeExchange(), config.getTradeExchangeMarket());
            return;
        }

        OrderPlaceRunner task = new OrderPlaceRunner(config, orderPlaceService);
        taskMap.put(config.getId(), task);

        String threadName = config.getTradeExchange().getName() + "#" + config.getTradeSymbol();
        executor.execute(ExecutorUtil.withName(ExecutorUtil.withTraceId(task), threadName));
    }

}
