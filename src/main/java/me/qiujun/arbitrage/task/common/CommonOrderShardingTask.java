package me.qiujun.arbitrage.task.common;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.system.master",
        havingValue = "true",
        matchIfMissing = true
)
public class CommonOrderShardingTask {

    @Autowired
    private OrderService orderService;

    @Scheduled(fixedRate = 60 * 1000)
    public void sharing() {
        orderService.sharding();
    }

}
