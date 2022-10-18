package me.qiujun.arbitrage.adapter.gate;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.Starter;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotBalanceResponse;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotOrderResponse;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotSymbolResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class GateHttpClientTest {

    @Autowired
    private GateSpotHttpClient gateSpotHttpClient;

    @Test
    public void testGetOrder() {
        GateSpotOrderResponse order = gateSpotHttpClient.getOrder("198920303008", "LUNC_USDT");
        log.info("Order is {}", order);
    }

    @Test
    public void testListBalances() {
        List<GateSpotBalanceResponse> balances = gateSpotHttpClient.listBalances();
        log.info("balance is {}", balances);
    }

    @Test
    public void testListSymbols() {
        List<GateSpotSymbolResponse> symbols = gateSpotHttpClient.listSymbols();
        log.info("symbols is {}", symbols);
    }

    @Test
    public void testCancelOrder() {
        Boolean result = gateSpotHttpClient.cancelOrder("198920303008", "LUNC_USDT");
        log.info("result is {}", result);
    }

}
