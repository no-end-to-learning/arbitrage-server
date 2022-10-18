package me.qiujun.arbitrage.adapter.binance.impl;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesHttpClient;
import me.qiujun.arbitrage.adapter.binance.bean.request.BinanceOrderCreateRequest;
import me.qiujun.arbitrage.adapter.binance.bean.request.BinanceTradeFilterRequest;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceBalanceResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceExchangeInfoResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceFuturesOrderResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceTradeResponse;
import me.qiujun.arbitrage.util.BeanUtil;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BinanceFuturesHttpClientImpl implements BinanceFuturesHttpClient {

    private final BinanceFuturesHttpDefinition apiService;

    public BinanceFuturesHttpClientImpl(OkHttpClient client, String httpBaseUrl, String apiKey, String secret) {
        apiService = BinanceExecutor.createService(BinanceFuturesHttpDefinition.class, client, httpBaseUrl, apiKey, secret);
    }

    @Override
    public BinanceExchangeInfoResponse getExchangeInfo() {
        return BinanceExecutor.execute(apiService.getExchangeInfo());
    }

    @Override
    public List<BinanceBalanceResponse> listBalances() {
        return BinanceExecutor.execute(apiService.listBalances());
    }

    @Override
    public BinanceFuturesOrderResponse createOrder(BinanceOrderCreateRequest params) {
        return BinanceExecutor.execute(apiService.createOrder(BeanUtil.bean2Map(params)));
    }

    @Override
    public BinanceFuturesOrderResponse getOrder(String symbol, String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol.toUpperCase());
        params.put("orderId", id);
        return BinanceExecutor.execute(apiService.getOrder(params));
    }

    @Override
    public List<BinanceTradeResponse> listTrades(BinanceTradeFilterRequest filter) {
        return BinanceExecutor.execute(apiService.listTrades(BeanUtil.bean2Map(filter)));
    }

}
