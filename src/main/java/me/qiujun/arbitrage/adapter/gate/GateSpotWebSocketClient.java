package me.qiujun.arbitrage.adapter.gate;

import me.qiujun.arbitrage.adapter.gate.bean.event.GateBalanceEvent;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateBookTickerEvent;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateEvent;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateOrderEvent;

import java.io.Closeable;
import java.util.List;

public interface GateSpotWebSocketClient {

    Closeable onBookTickerEvent(List<String> symbols, GateWebSocketCallback<GateEvent<GateBookTickerEvent>> callback);

    Closeable onOrderEvent(GateWebSocketCallback<GateEvent<List<GateOrderEvent>>> callback);

    Closeable onBalanceEvent(GateWebSocketCallback<GateEvent<List<GateBalanceEvent>>> callback);

}
