package me.qiujun.arbitrage.task.gate;

import me.qiujun.arbitrage.Starter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class GateSpotAssetWatchTaskTest {

    @Autowired
    private GateSpotAssetWatchTask gateSpotAssetWatchTask;

    @Test
    public void testExecute() {
        gateSpotAssetWatchTask.execute();
    }
}
