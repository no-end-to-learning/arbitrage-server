package me.qiujun.arbitrage.adapter.gate;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.gate.impl.GateSpotHttpClientImpl;
import me.qiujun.arbitrage.adapter.gate.impl.GateSpotWebSocketClientImpl;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class GateConfiguration {

    @Autowired
    private GateProperties props;

    @Autowired
    public OkHttpClient sharedOkHttpClient;

    @Bean
    public GateSpotHttpClient gateSpotHttpClient() {
        OkHttpClient okHttpClient = getAdapterHttpClient();
        return new GateSpotHttpClientImpl(okHttpClient, props.getHttpBaseUrl(), props.getApiKey(), props.getSecretKey());
    }

    @Bean
    public GateSpotWebSocketClient gateSpotWebSocketClient() {
        OkHttpClient okHttpClient = getAdapterWebSocketClient();
        return new GateSpotWebSocketClientImpl(okHttpClient, props.getWebSocketBaseUrl(), props.getApiKey(), props.getSecretKey());
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
