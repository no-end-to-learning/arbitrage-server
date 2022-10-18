package me.qiujun.arbitrage.adapter.gate;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.Starter;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateBalanceEvent;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateOrderEvent;
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
public class GateSpotWebSocketClientTest {

    @Autowired
    private GateSpotWebSocketClient gateSpotWebSocketClient;

    @Test
    public void testListenBookTickerEvent() throws InterruptedException {
        List<String> symbols = Collections.singletonList("BTC_USDT");
        gateSpotWebSocketClient.onBookTickerEvent(symbols, (event) -> {
            log.info("{}", event);
        });

        Thread.sleep(10000000);
    }


    @Test
    public void testListenOrderEvent() throws InterruptedException {
        gateSpotWebSocketClient.onOrderEvent((event) -> {
            for (GateOrderEvent orderEvent : event.getResult()) {
                log.info("{}", orderEvent.transformTo());
            }
        });

        Thread.sleep(10000000);
    }

    @Test
    public void testListenBalanceEvent() throws InterruptedException {
        gateSpotWebSocketClient.onBalanceEvent((event) -> {
            for (GateBalanceEvent balanceEvent : event.getResult()) {
                log.info("{}", balanceEvent);
            }
        });

        Thread.sleep(10000000);
    }
}
