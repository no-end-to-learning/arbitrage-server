package me.qiujun.arbitrage.task.binance;

import me.qiujun.arbitrage.Starter;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.event.CurrentConfigsChangeEvent;
import me.qiujun.arbitrage.service.ConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class BinanceFuturesDepthWatchTaskTest {

    @Autowired
    private ConfigService configService;

    @Autowired
    private BinanceFuturesDepthWatchTask binanceFuturesDepthWatchTask;

    @Test
    public void testExecute() throws IOException, InterruptedException {
        List<Config> configs = configService.list();

        CurrentConfigsChangeEvent event = new CurrentConfigsChangeEvent(this, null, configs);

        binanceFuturesDepthWatchTask.onCurrentConfigsChangeEvent(event);

        Thread.sleep(10000);
    }

}
