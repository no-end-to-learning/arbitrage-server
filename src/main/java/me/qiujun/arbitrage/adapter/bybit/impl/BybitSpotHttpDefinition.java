package me.qiujun.arbitrage.adapter.bybit.impl;

import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitOrderCancelByIdsRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitOrderCancelRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitOrderCreateRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.response.*;
import me.qiujun.arbitrage.adapter.bybit.constant.BybitConstants;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface BybitSpotHttpDefinition {

    @GET("/spot/v3/public/symbols")
    Call<BybitBaseResponse<BybitSymbolResponse>> listSymbols();

    @GET("/spot/v3/private/account")
    @Headers(BybitConstants.HEADER_SIGNATURE_REQUIRED)
    Call<BybitBaseResponse<BybitBalanceResponse>> listBalances();

    @POST("/spot/v3/private/order")
    @Headers(BybitConstants.HEADER_SIGNATURE_REQUIRED)
    Call<BybitBaseResponse<BybitOrderResponse>> createOrder(@Body BybitOrderCreateRequest body);

    @POST("/spot/v3/private/cancel-orders")
    @Headers(BybitConstants.HEADER_SIGNATURE_REQUIRED)
    Call<BybitBaseResponse<BybitOrderCancelResponse>> cancelOrder(@Body BybitOrderCancelRequest body);

    @POST("/spot/v3/private/cancel-orders-by-ids")
    @Headers(BybitConstants.HEADER_SIGNATURE_REQUIRED)
    Call<BybitBaseResponse<BybitOrderCancelByIdsResponse>> cancelOrdersByIds(@Body BybitOrderCancelByIdsRequest body);

    @GET("/spot/v3/private/order")
    @Headers(BybitConstants.HEADER_SIGNATURE_REQUIRED)
    Call<BybitBaseResponse<BybitOrderResponse>> getOrder(@Query("orderId") String id);

    @GET("/spot/v3/private/my-trades")
    @Headers(BybitConstants.HEADER_SIGNATURE_REQUIRED)
    Call<BybitBaseResponse<BybitTradeResponse>> listTrades(@QueryMap Map<String, Object> params);

}
