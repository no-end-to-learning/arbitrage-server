package me.qiujun.arbitrage.adapter.bybit.impl;

import com.alibaba.fastjson.TypeReference;
import me.qiujun.arbitrage.adapter.bybit.BybitSpotWebSocketClient;
import me.qiujun.arbitrage.adapter.bybit.BybitWebSocketCallback;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitAccountEvent;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitBookTickerEvent;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitEvent;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitOrderEvent;
import okhttp3.OkHttpClient;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;

public class BybitSpotWebSocketClientImpl extends BybitWebSocketClientImpl implements BybitSpotWebSocketClient {

    public BybitSpotWebSocketClientImpl(OkHttpClient client, String baseUrl, String apiKey, String secretKey) {
        super(client, baseUrl, apiKey, secretKey);
    }

    @Override
    public Closeable onBookTickerEvent(List<String> symbols, BybitWebSocketCallback<BybitEvent<BybitBookTickerEvent>> callback) {
        List<String> topics = symbols.stream()
                .map(item -> "bookticker." + item)
                .toList();
        TypeReference<BybitEvent<BybitBookTickerEvent>> typeReference = new TypeReference<>() {
        };
        return createConnection("/spot/public/v3", topics, callback, typeReference);
    }

    @Override
    public Closeable onOrderEvent(BybitWebSocketCallback<BybitEvent<List<BybitOrderEvent>>> callback) {
        TypeReference<BybitEvent<List<BybitOrderEvent>>> typeReference = new TypeReference<>() {
        };
        return createConnection("/spot/private/v3", Collections.singletonList("order"), callback, typeReference);
    }

    @Override
    public Closeable onAccountEvent(BybitWebSocketCallback<BybitEvent<List<BybitAccountEvent>>> callback) {
        TypeReference<BybitEvent<List<BybitAccountEvent>>> typeReference = new TypeReference<>() {
        };
        return createConnection("/spot/private/v3", Collections.singletonList("outboundAccountInfo"), callback, typeReference);
    }

}
