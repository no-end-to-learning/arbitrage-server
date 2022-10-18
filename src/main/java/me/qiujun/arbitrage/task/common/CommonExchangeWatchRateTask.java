package me.qiujun.arbitrage.task.common;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceSpotHttpClient;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceCandlestickResponse;
import me.qiujun.arbitrage.service.MarketPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CommonExchangeWatchRateTask {

    @Autowired
    private BinanceSpotHttpClient spotHttpClient;

    @Autowired
    private MarketPriceService marketPriceService;

    @Scheduled(fixedRate = 15 * 1000)
    public void execute() {
        Map<String, String> symbolMap = new HashMap<>();
        symbolMap.put("BUSDUSDT", "BUSD_USDT");

        for (String symbol : symbolMap.keySet()) {
            List<BinanceCandlestickResponse> candlesticks = spotHttpClient.listCandlesticks(symbol, "1m");

            BigDecimal finalExchangeRate = null;
            List<Integer> spans = Arrays.asList(15, 30);
            for (Integer span : spans) {
                BigDecimal totalExchangeRate = BigDecimal.ZERO;
                BigDecimal exchangeRateSize = BigDecimal.ZERO;
                int totalSize = candlesticks.size();
                for (int i = totalSize - span; i < totalSize; i++) {
                    exchangeRateSize = exchangeRateSize.add(BigDecimal.ONE);
                    totalExchangeRate = totalExchangeRate.add(candlesticks.get(i).getOpen());
                }

                BigDecimal avgExchangeRate = totalExchangeRate.divide(exchangeRateSize, 18, RoundingMode.HALF_EVEN);
                if (finalExchangeRate == null || avgExchangeRate.compareTo(finalExchangeRate) > 0) {
                    finalExchangeRate = avgExchangeRate;
                }
            }

            marketPriceService.setExchangeRate(symbolMap.get(symbol), finalExchangeRate);
        }

        marketPriceService.setExchangeRate("USDT_USD", new BigDecimal("1"));
        marketPriceService.setMarketPrice("USDT_USD", new BigDecimal("1"));
    }
}
