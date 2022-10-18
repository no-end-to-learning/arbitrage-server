package me.qiujun.arbitrage.adapter.gate;

import me.qiujun.arbitrage.adapter.gate.bean.request.GateSpotOrderCreateRequest;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotBalanceResponse;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotOrderResponse;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotSymbolResponse;

import java.util.List;

public interface GateSpotHttpClient {

    List<GateSpotSymbolResponse> listSymbols();

    GateSpotOrderResponse createOrder(GateSpotOrderCreateRequest body);

    Boolean cancelOrder(String id, String currencyPair);

    GateSpotOrderResponse getOrder(String id, String currencyPair);

    List<GateSpotBalanceResponse> listBalances();

}
