package me.qiujun.arbitrage.adapter.bybit;

import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitAccountEvent;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitBookTickerEvent;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitEvent;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitOrderEvent;

import java.io.Closeable;
import java.util.List;

public interface BybitSpotWebSocketClient {

    Closeable onBookTickerEvent(List<String> symbols, BybitWebSocketCallback<BybitEvent<BybitBookTickerEvent>> callback);

    Closeable onOrderEvent(BybitWebSocketCallback<BybitEvent<List<BybitOrderEvent>>> callback);

    Closeable onAccountEvent(BybitWebSocketCallback<BybitEvent<List<BybitAccountEvent>>> callback);

}
