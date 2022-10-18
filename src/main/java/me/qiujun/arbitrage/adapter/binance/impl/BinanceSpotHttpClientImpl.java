package me.qiujun.arbitrage.adapter.binance.impl;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceSpotHttpClient;
import me.qiujun.arbitrage.adapter.binance.bean.request.BinanceCandlestickRequest;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceCandlestickResponse;
import me.qiujun.arbitrage.util.BeanUtil;
import okhttp3.OkHttpClient;

import java.util.List;

@Slf4j
public class BinanceSpotHttpClientImpl implements BinanceSpotHttpClient {

    private final BinanceSpotHttpDefinition apiService;

    public BinanceSpotHttpClientImpl(OkHttpClient client, String httpBaseUrl, String apiKey, String secret) {
        apiService = BinanceExecutor.createService(BinanceSpotHttpDefinition.class, client, httpBaseUrl, apiKey, secret);
    }

    @Override
    public List<BinanceCandlestickResponse> listCandlesticks(String symbol, String interval) {
        BinanceCandlestickRequest request = BinanceCandlestickRequest.builder()
                .symbol(symbol)
                .interval(interval)
                .build();
        return listCandlesticks(request);
    }

    @Override
    public List<BinanceCandlestickResponse> listCandlesticks(BinanceCandlestickRequest request) {
        return BinanceExecutor.execute(apiService.listCandlesticks(BeanUtil.bean2Map(request)));
    }
}
