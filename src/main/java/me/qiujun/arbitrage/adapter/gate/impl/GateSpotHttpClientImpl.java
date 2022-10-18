package me.qiujun.arbitrage.adapter.gate.impl;

import me.qiujun.arbitrage.adapter.gate.GateSpotHttpClient;
import me.qiujun.arbitrage.adapter.gate.bean.request.GateSpotOrderCreateRequest;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotBalanceResponse;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotOrderResponse;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotSymbolResponse;
import okhttp3.OkHttpClient;

import java.util.List;

public class GateSpotHttpClientImpl implements GateSpotHttpClient {

    private final GateSpotHttpDefinition apiService;

    public GateSpotHttpClientImpl(OkHttpClient client, String httpBaseUrl, String apiKey, String secretKey) {
        apiService = GateHttpExecutor.createService(GateSpotHttpDefinition.class, client, httpBaseUrl, apiKey, secretKey);
    }

    @Override
    public List<GateSpotSymbolResponse> listSymbols() {
        return GateHttpExecutor.execute(apiService.listSymbols());
    }

    @Override
    public GateSpotOrderResponse createOrder(GateSpotOrderCreateRequest body) {
        return GateHttpExecutor.execute(apiService.createOrder(body));
    }

    @Override
    public Boolean cancelOrder(String id, String currencyPair) {
        return GateHttpExecutor.execute(apiService.cancelOrder(id, currencyPair)) != null;
    }

    @Override
    public GateSpotOrderResponse getOrder(String id, String currencyPair) {
        return GateHttpExecutor.execute(apiService.getOrder(id, currencyPair));
    }

    @Override
    public List<GateSpotBalanceResponse> listBalances() {
        return GateHttpExecutor.execute(apiService.listBalances());
    }

}
