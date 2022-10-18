package me.qiujun.arbitrage.adapter.bybit;

public interface BybitWebSocketCallback<T> {

    void onMessage(T response);

}