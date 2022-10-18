package me.qiujun.arbitrage.adapter.bybit;

import me.qiujun.arbitrage.adapter.bybit.impl.BybitSpotHttpClientImpl;
import me.qiujun.arbitrage.adapter.bybit.impl.BybitSpotWebSocketClientImpl;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class BybitConfiguration {

    @Autowired
    private BybitProperties props;

    @Autowired
    private OkHttpClient sharedOkHttpClient;

    @Bean
    public BybitSpotHttpClient bybitSpotHttpClient() {
        OkHttpClient okHttpClient = getAdapterHttpClient();
        return new BybitSpotHttpClientImpl(okHttpClient, props.getHttpBaseUrl(), props.getApiKey(), props.getSecretKey());
    }

    @Bean
    public BybitSpotWebSocketClient bybitSpotWebSocketClient() {
        OkHttpClient okHttpClient = getAdapterWebSocketClient();
        return new BybitSpotWebSocketClientImpl(okHttpClient, props.getWebSocketBaseUrl(), props.getApiKey(), props.getSecretKey());
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
