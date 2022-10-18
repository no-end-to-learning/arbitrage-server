package me.qiujun.arbitrage.task.common;

import me.qiujun.arbitrage.Starter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class CommonExchangeWatchRateTaskTest {

    @Autowired
    private CommonExchangeWatchRateTask commonExchangeWatchRateTask;

    @Test
    public void testExecute() {
        commonExchangeWatchRateTask.execute();
    }

}
