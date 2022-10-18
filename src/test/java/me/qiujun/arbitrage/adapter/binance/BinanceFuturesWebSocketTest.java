package me.qiujun.arbitrage.adapter.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.Starter;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesWebSocketClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class BinanceFuturesWebSocketTest {

    @Autowired
    private BinanceFuturesWebSocketClient client;

    @Test
    public void testOnDepthEvent() throws InterruptedException {
        client.onBookTickerEvent("BTCUSDT", (depth) -> {
            log.info("{}", depth);
        });

        Thread.sleep(10000000);
    }

}
