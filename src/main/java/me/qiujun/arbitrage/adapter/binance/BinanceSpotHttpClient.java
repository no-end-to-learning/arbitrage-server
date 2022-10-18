package me.qiujun.arbitrage.adapter.binance;

import me.qiujun.arbitrage.adapter.binance.bean.request.BinanceCandlestickRequest;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceCandlestickResponse;

import java.util.List;

public interface BinanceSpotHttpClient {

    List<BinanceCandlestickResponse> listCandlesticks(String symbol, String interval);

    List<BinanceCandlestickResponse> listCandlesticks(BinanceCandlestickRequest request);

}
