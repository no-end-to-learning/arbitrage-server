package me.qiujun.arbitrage.adapter.binance.impl;

import me.qiujun.arbitrage.adapter.binance.BinanceFuturesWebSocketClient;
import me.qiujun.arbitrage.adapter.binance.BinanceWebSocketCallback;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceFuturesBookTickerEvent;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceFuturesDepthEvent;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceEvent;
import okhttp3.OkHttpClient;

import java.io.Closeable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BinanceFuturesWebSocketClientImpl extends BinanceWebSocketClientImpl implements BinanceFuturesWebSocketClient {

    public BinanceFuturesWebSocketClientImpl(OkHttpClient client, String baseUrl) {
        super(client, baseUrl);
    }

    @Override
    public Closeable onBookTickerEvent(String symbols, BinanceWebSocketCallback<BinanceEvent<BinanceFuturesBookTickerEvent>> callback) {
        String channel = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .map(s -> String.format("%s@bookTicker", s))
                .collect(Collectors.joining("/"));
        return createConnection(channel, callback, BinanceFuturesBookTickerEvent.class);
    }

    @Override
    public Closeable onDepthEvent(String symbols, BinanceWebSocketCallback<BinanceEvent<BinanceFuturesDepthEvent>> callback) {
        String channel = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .map(s -> String.format("%s@depth5@100ms", s))
                .collect(Collectors.joining("/"));
        return createConnection(channel, callback, BinanceFuturesDepthEvent.class);
    }

}
