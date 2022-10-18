package me.qiujun.arbitrage.adapter.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.impl.BinanceFuturesHttpClientImpl;
import me.qiujun.arbitrage.adapter.binance.impl.BinanceFuturesWebSocketClientImpl;
import me.qiujun.arbitrage.adapter.binance.impl.BinanceSpotHttpClientImpl;
import me.qiujun.arbitrage.adapter.binance.impl.BinanceSpotWebSocketClientImpl;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class BinanceConfiguration {

    @Autowired
    private BinanceProperties props;

    @Autowired
    public OkHttpClient sharedOkHttpClient;

    @Bean
    public BinanceSpotHttpClient binanceSpotHttpClient() {
        OkHttpClient okHttpClient = getAdapterHttpClient();
        return new BinanceSpotHttpClientImpl(okHttpClient, props.getSpotHttpBaseUrl(), props.getApiKey(), props.getSecret());
    }

    @Bean
    public BinanceSpotWebSocketClient binanceSpotWebSocketClient() {
        OkHttpClient okHttpClient = getAdapterWebSocketClient();
        return new BinanceSpotWebSocketClientImpl(okHttpClient, props.getSpotWebSocketBaseUrl());
    }

    @Bean
    public BinanceFuturesHttpClient binanceFuturesHttpClient() {
        OkHttpClient okHttpClient = getAdapterHttpClient();
        return new BinanceFuturesHttpClientImpl(okHttpClient, props.getFuturesHttpBaseUrl(), props.getApiKey(), props.getSecret());
    }

    @Bean
    public BinanceFuturesWebSocketClient binanceFuturesWebSocketClient() {
        OkHttpClient okHttpClient = getAdapterWebSocketClient();
        return new BinanceFuturesWebSocketClientImpl(okHttpClient, props.getFuturesWebSocketBaseUrl());
    }

    private OkHttpClient getAdapterHttpClient() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);

        return sharedOkHttpClient.newBuilder()
                .dispatcher(dispatcher)
                .build();
    }

    private OkHttpClient getAdapterWebSocketClient() {
        return sharedOkHttpClient.newBuilder()
                .pingInterval(20, TimeUnit.SECONDS)
                .build();
    }

}
