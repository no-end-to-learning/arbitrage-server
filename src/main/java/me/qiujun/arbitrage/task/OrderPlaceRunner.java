package me.qiujun.arbitrage.task;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Config;

@Slf4j
public class OrderPlaceRunner implements Runnable {

    private boolean stop = false;

    private final Config config;

    private final OrderPlaceAbstract service;

    public OrderPlaceRunner(Config config, OrderPlaceAbstract service) {
        this.config = config;
        this.service = service;
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        log.info("start trade task, config id {}", config.getId());

        while (!stop && !Thread.currentThread().isInterrupted()) {
            try {

                long startAt = System.currentTimeMillis();
                service.execute(config);
                long endAt = System.currentTimeMillis();

                long timeCost = endAt - startAt;
                if (timeCost > config.getTradeIntervalThreshold()) {
                    log.info("order place execute cost {}ms, config id {}", timeCost, config.getId());
                }

                if (!stop && !Thread.currentThread().isInterrupted()) {
                    long sleepTimeMillis = config.getTradeInterval() - timeCost;
                    if (sleepTimeMillis > 0) {
                        Thread.sleep(sleepTimeMillis);
                    }
                }

            } catch (Exception e) {
                log.error("trade task error", e);
            }
        }

        log.info("start cancel all orders, config id {}", config.getId());
        service.cancelAllOrders(config);
        log.info("all orders canceled, config id {}", config.getId());

        log.info("trade task stopped, config id {}", config.getId());
    }

}
