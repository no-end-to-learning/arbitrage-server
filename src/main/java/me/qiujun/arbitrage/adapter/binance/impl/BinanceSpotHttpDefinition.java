package me.qiujun.arbitrage.adapter.binance.impl;

import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceCandlestickResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.List;
import java.util.Map;

public interface BinanceSpotHttpDefinition {

    @GET("/api/v3/klines")
    Call<List<BinanceCandlestickResponse>> listCandlesticks(@QueryMap Map<String, Object> params);

}
