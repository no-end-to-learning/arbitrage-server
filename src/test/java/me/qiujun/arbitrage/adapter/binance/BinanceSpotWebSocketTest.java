package me.qiujun.arbitrage.adapter.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.Starter;
import me.qiujun.arbitrage.adapter.binance.BinanceSpotWebSocketClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class BinanceSpotWebSocketTest {

    @Autowired
    private BinanceSpotWebSocketClient spotWebSocketClient;

    @Test
    public void testOnDepthEvent() throws InterruptedException {
        spotWebSocketClient.onDepthEvent("BTCUSDT,ETHUSDT", (depth) -> {
            // BigDecimal askPrice = depth.getAsks().get(0).getPrice();
            // BigDecimal bidPrice = depth.getBids().get(0).getPrice();
            // BigDecimal exchangePrice = askPrice.add(bidPrice).divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN);

            log.info("{}", depth);
        });

        Thread.sleep(10000000);
    }

}
