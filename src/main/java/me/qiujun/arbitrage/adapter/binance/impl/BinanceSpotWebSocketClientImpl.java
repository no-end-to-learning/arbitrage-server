package me.qiujun.arbitrage.adapter.binance.impl;

import me.qiujun.arbitrage.adapter.binance.BinanceSpotWebSocketClient;
import me.qiujun.arbitrage.adapter.binance.BinanceWebSocketCallback;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceSpotDepthEvent;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceEvent;
import okhttp3.OkHttpClient;

import java.io.Closeable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BinanceSpotWebSocketClientImpl extends BinanceWebSocketClientImpl implements BinanceSpotWebSocketClient {

    public BinanceSpotWebSocketClientImpl(OkHttpClient client, String baseUrl) {
        super(client, baseUrl);
    }

    @Override
    public Closeable onDepthEvent(String symbols, BinanceWebSocketCallback<BinanceEvent<BinanceSpotDepthEvent>> callback) {
        String channel = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .map(s -> String.format("%s@depth5", s))
                .collect(Collectors.joining("/"));
        return createConnection(channel, callback, BinanceSpotDepthEvent.class);
    }

}
