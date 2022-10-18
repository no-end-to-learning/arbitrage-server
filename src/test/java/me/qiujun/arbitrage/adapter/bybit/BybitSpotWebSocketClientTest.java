package me.qiujun.arbitrage.adapter.bybit;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.Starter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class BybitSpotWebSocketClientTest {

    @Autowired
    private BybitSpotWebSocketClient bybitSpotWebSocketClient;

    @Test
    public void testOnBookTickerEvent() throws InterruptedException {
        List<String> symbols = Collections.singletonList("BTCUSDT");
        bybitSpotWebSocketClient.onBookTickerEvent(symbols, (event) -> {
            log.info("{}", event);
        });

        Thread.sleep(10000000);
    }


    @Test
    public void testOnAccountEvent() throws InterruptedException {
        bybitSpotWebSocketClient.onAccountEvent((event) -> {
            log.info("{}", event);
        });

        Thread.sleep(10000000);
    }

}
