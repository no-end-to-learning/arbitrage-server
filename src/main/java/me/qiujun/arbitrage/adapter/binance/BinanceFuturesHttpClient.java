package me.qiujun.arbitrage.adapter.binance;

import me.qiujun.arbitrage.adapter.binance.bean.request.BinanceOrderCreateRequest;
import me.qiujun.arbitrage.adapter.binance.bean.request.BinanceTradeFilterRequest;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceBalanceResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceExchangeInfoResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceFuturesOrderResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceTradeResponse;

import java.util.List;

public interface BinanceFuturesHttpClient {

    BinanceExchangeInfoResponse getExchangeInfo();

    List<BinanceBalanceResponse> listBalances();

    BinanceFuturesOrderResponse createOrder(BinanceOrderCreateRequest params);

    BinanceFuturesOrderResponse getOrder(String symbol, String id);

    List<BinanceTradeResponse> listTrades(BinanceTradeFilterRequest filter);

}
