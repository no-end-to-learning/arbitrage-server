package me.qiujun.arbitrage.adapter.bybit.impl;

import me.qiujun.arbitrage.adapter.bybit.BybitSpotHttpClient;
import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitOrderCancelByIdsRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitOrderCancelRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitOrderCreateRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitTradeRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.response.*;
import me.qiujun.arbitrage.util.BeanUtil;
import okhttp3.OkHttpClient;

import java.util.List;

public class BybitSpotHttpClientImpl implements BybitSpotHttpClient {

    private final BybitSpotHttpDefinition apiService;

    public BybitSpotHttpClientImpl(OkHttpClient client, String httpBaseUrl, String apiKey, String secretKey) {
        apiService = BybitHttpExecutor.createService(BybitSpotHttpDefinition.class, client, httpBaseUrl, apiKey, secretKey);
    }

    @Override
    public List<BybitSymbolResponse.Item> listSymbols() {
        return BybitHttpExecutor.execute(apiService.listSymbols()).getResult().getList();
    }

    @Override
    public List<BybitBalanceResponse.Item> listBalances() {
        return BybitHttpExecutor.execute(apiService.listBalances()).getResult().getBalances();
    }

    @Override
    public BybitOrderResponse createOrder(BybitOrderCreateRequest body) {
        return BybitHttpExecutor.execute(apiService.createOrder(body)).getResult();
    }

    @Override
    public Boolean cancelOrder(String id) {
        BybitOrderCancelRequest body = new BybitOrderCancelRequest(id);
        return BybitHttpExecutor.execute(apiService.cancelOrder(body)).getResult().getSuccess().equals("1");
    }

    @Override
    public List<BybitOrderCancelByIdsResponse.Item> cancelOrder(List<String> ids) {
        BybitOrderCancelByIdsRequest body = new BybitOrderCancelByIdsRequest(ids);
        return BybitHttpExecutor.execute(apiService.cancelOrdersByIds(body)).getResult().getList();
    }

    @Override
    public BybitOrderResponse getOrder(String id) {
        return BybitHttpExecutor.execute(apiService.getOrder(id)).getResult();
    }

    @Override
    public List<BybitTradeResponse.Item> listTrades(String orderId) {
        BybitTradeRequest params = new BybitTradeRequest(orderId);
        return BybitHttpExecutor.execute(apiService.listTrades(BeanUtil.bean2Map(params))).getResult().getList();
    }
}
