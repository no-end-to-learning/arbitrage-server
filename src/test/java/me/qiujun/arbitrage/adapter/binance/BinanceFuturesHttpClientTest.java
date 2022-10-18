package me.qiujun.arbitrage.adapter.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.Starter;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesHttpClient;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceBalanceResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class BinanceFuturesHttpClientTest {

    @Autowired
    private BinanceFuturesHttpClient binanceFuturesHttpClient;

    @Test
    public void testListBalance() {
        List<BinanceBalanceResponse> balances = binanceFuturesHttpClient.listBalances();
        System.out.println(balances);
    }

}
