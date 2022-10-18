package me.qiujun.arbitrage.adapter.binance;

import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceFuturesBookTickerEvent;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceFuturesDepthEvent;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceEvent;

import java.io.Closeable;

public interface BinanceFuturesWebSocketClient {

    Closeable onBookTickerEvent(String symbols, BinanceWebSocketCallback<BinanceEvent<BinanceFuturesBookTickerEvent>> callback);

    Closeable onDepthEvent(String symbols, BinanceWebSocketCallback<BinanceEvent<BinanceFuturesDepthEvent>> callback);

}
