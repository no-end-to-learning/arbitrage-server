package me.qiujun.arbitrage.adapter.gate.impl;

import me.qiujun.arbitrage.adapter.gate.bean.request.GateSpotOrderCreateRequest;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotBalanceResponse;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotOrderResponse;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotSymbolResponse;
import me.qiujun.arbitrage.adapter.gate.constant.GateConstants;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface GateSpotHttpDefinition {

    @GET("/api/v4/spot/currency_pairs")
    Call<List<GateSpotSymbolResponse>> listSymbols();

    @GET("/api/v4/spot/accounts")
    @Headers(GateConstants.HEADER_SIGNATURE_REQUIRED)
    Call<List<GateSpotBalanceResponse>> listBalances();

    @POST("/api/v4/spot/orders")
    @Headers(GateConstants.HEADER_SIGNATURE_REQUIRED)
    Call<GateSpotOrderResponse> createOrder(@Body GateSpotOrderCreateRequest body);

    @DELETE("/api/v4/spot/orders/{order_id}")
    @Headers(GateConstants.HEADER_SIGNATURE_REQUIRED)
    Call<GateSpotOrderResponse> cancelOrder(@Path("order_id") String id, @Query("currency_pair") String currencyPair);

    @GET("/api/v4/spot/orders/{order_id}")
    @Headers(GateConstants.HEADER_SIGNATURE_REQUIRED)
    Call<GateSpotOrderResponse> getOrder(@Path("order_id") String id, @Query("currency_pair") String currencyPair);

}
