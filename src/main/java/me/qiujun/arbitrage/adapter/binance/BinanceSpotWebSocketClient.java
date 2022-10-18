package me.qiujun.arbitrage.adapter.binance;

import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceSpotDepthEvent;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceEvent;

import java.io.Closeable;

public interface BinanceSpotWebSocketClient {

    Closeable onDepthEvent(String symbols, BinanceWebSocketCallback<BinanceEvent<BinanceSpotDepthEvent>> callback);

}
