package me.qiujun.arbitrage.adapter.binance.impl;

import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceBalanceResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceExchangeInfoResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceFuturesOrderResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceTradeResponse;
import me.qiujun.arbitrage.adapter.binance.constant.BinanceApiConstants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

import java.util.List;
import java.util.Map;

public interface BinanceFuturesHttpDefinition {

    @GET("/fapi/v1/exchangeInfo")
    Call<BinanceExchangeInfoResponse> getExchangeInfo();

    @GET("/fapi/v2/balance")
    @Headers(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    Call<List<BinanceBalanceResponse>> listBalances();

    @POST("/fapi/v1/order")
    @Headers(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    Call<BinanceFuturesOrderResponse> createOrder(@QueryMap Map<String, Object> params);

    @GET("/fapi/v1/order")
    @Headers(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    Call<BinanceFuturesOrderResponse> getOrder(@QueryMap Map<String, Object> params);

    @GET("/fapi/v1/userTrades")
    @Headers(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    Call<List<BinanceTradeResponse>> listTrades(@QueryMap Map<String, Object> params);
}
