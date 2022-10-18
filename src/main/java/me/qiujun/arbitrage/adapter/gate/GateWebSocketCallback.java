package me.qiujun.arbitrage.adapter.gate;

public interface GateWebSocketCallback<T> {

    void onMessage(T response);

}