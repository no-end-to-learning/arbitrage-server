package me.qiujun.arbitrage.adapter.binance;

public interface BinanceWebSocketCallback<T> {

    void onMessage(T response);

}