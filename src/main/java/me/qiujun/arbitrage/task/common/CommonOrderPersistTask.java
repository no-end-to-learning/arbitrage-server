package me.qiujun.arbitrage.task.common;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommonOrderPersistTask {

    @Autowired
    private OrderService orderService;

    @Scheduled(fixedRate = 1000)
    public void persist() {
        orderService.persist();
    }

}
