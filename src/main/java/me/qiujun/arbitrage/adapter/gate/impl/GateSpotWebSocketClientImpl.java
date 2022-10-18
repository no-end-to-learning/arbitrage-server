package me.qiujun.arbitrage.adapter.gate.impl;

import com.alibaba.fastjson.TypeReference;
import me.qiujun.arbitrage.adapter.gate.GateSpotWebSocketClient;
import me.qiujun.arbitrage.adapter.gate.GateWebSocketCallback;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateBalanceEvent;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateBookTickerEvent;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateEvent;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateOrderEvent;
import okhttp3.OkHttpClient;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;

public class GateSpotWebSocketClientImpl extends GateWebSocketClientImpl implements GateSpotWebSocketClient {

    public GateSpotWebSocketClientImpl(OkHttpClient client, String baseUrl, String apiKey, String secretKey) {
        super(client, baseUrl, apiKey, secretKey);
    }

    @Override
    public Closeable onBookTickerEvent(List<String> symbols, GateWebSocketCallback<GateEvent<GateBookTickerEvent>> callback) {
        TypeReference<GateEvent<GateBookTickerEvent>> typeReference = new TypeReference<>() {
        };
        return createConnection(false, "spot.book_ticker", symbols, callback, typeReference);
    }

    @Override
    public Closeable onOrderEvent(GateWebSocketCallback<GateEvent<List<GateOrderEvent>>> callback) {
        TypeReference<GateEvent<List<GateOrderEvent>>> typeReference = new TypeReference<>() {
        };
        List<String> allSymbols = Collections.singletonList("!all");
        return createConnection(true, "spot.orders", allSymbols, callback, typeReference);
    }

    @Override
    public Closeable onBalanceEvent(GateWebSocketCallback<GateEvent<List<GateBalanceEvent>>> callback) {
        TypeReference<GateEvent<List<GateBalanceEvent>>> typeReference = new TypeReference<>() {
        };
        List<String> allSymbols = Collections.singletonList("!all");
        return createConnection(true, "spot.balances", allSymbols, callback, typeReference);
    }

}
