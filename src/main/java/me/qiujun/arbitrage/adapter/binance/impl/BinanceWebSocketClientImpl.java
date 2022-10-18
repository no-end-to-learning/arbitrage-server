package me.qiujun.arbitrage.adapter.binance.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.WebSocketClientWrapper;
import me.qiujun.arbitrage.adapter.binance.BinanceWebSocketCallback;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

@Slf4j
public class BinanceWebSocketClientImpl {

    private final OkHttpClient client;

    private final String baseUrl;

    public BinanceWebSocketClientImpl(OkHttpClient client, String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    protected <T> Closeable createConnection(String channel, BinanceWebSocketCallback<BinanceEvent<T>> callback, Class<T> clazz) {
        String streamingUrl = String.format("%s/stream?streams=%s", this.baseUrl, channel);
        Request request = new Request.Builder().url(streamingUrl).build();

        return new WebSocketClientWrapper(client, request, new WebSocketListener() {

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                callback.onMessage(JSON.parseObject(text, new TypeReference<BinanceEvent<T>>(clazz) {
                }));
            }

        });
    }

}
