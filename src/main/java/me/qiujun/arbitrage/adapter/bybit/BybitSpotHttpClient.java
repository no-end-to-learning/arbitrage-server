package me.qiujun.arbitrage.adapter.bybit;

import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitOrderCreateRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.response.*;

import java.util.List;

public interface BybitSpotHttpClient {

    List<BybitSymbolResponse.Item> listSymbols();

    List<BybitBalanceResponse.Item> listBalances();

    BybitOrderResponse createOrder(BybitOrderCreateRequest body);

    Boolean cancelOrder(String id);

    List<BybitOrderCancelByIdsResponse.Item> cancelOrder(List<String> ids);

    BybitOrderResponse getOrder(String id);

    List<BybitTradeResponse.Item> listTrades(String orderId);

}
