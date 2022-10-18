package me.qiujun.arbitrage.task.common;

import me.qiujun.arbitrage.Starter;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.event.AnyConfigsChangeEvent;
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
public class CommonMarketPriceListenTaskTest {

    @Autowired
    private ConfigService configService;

    @Autowired
    private CommonMarketPriceListenTask commonMarketPriceListenTask;

    @Test
    public void testExecute() throws InterruptedException, IOException {
        List<Config> configs = configService.list();

        AnyConfigsChangeEvent event = new AnyConfigsChangeEvent(this, null, configs);
        commonMarketPriceListenTask.onAnyConfigsChangeEvent(event);

        Thread.sleep(10000);
    }
}
