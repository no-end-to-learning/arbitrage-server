package me.qiujun.arbitrage.service;

import me.qiujun.arbitrage.constant.MarketConstant;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketPriceService {

    private final Map<String, BigDecimal> priceMap = new ConcurrentHashMap<>();

    private final Map<String, BigDecimal> exchangeRateMap = new ConcurrentHashMap<>();


    public void setMarketPrice(String symbol, BigDecimal price) {
        priceMap.put(symbol, price);
    }

    public BigDecimal getMarketPrice(String symbol) {
        return priceMap.get(symbol);
    }


    public BigDecimal getValuationPrice(String currency) {
        return getValuationPrice(currency, MarketConstant.DEFAULT_CURRENCY);
    }

    public BigDecimal getValuationPrice(String baseCurrency, String quoteCurrency) {
        return getPriceRecursion(priceMap, baseCurrency, quoteCurrency);
    }


    public void setExchangeRate(String symbol, BigDecimal exchangeRate) {
        exchangeRateMap.put(symbol, exchangeRate);
    }

    public BigDecimal getExchangeRate(String tradeSymbol, String hedgeSymbol) {
        String tradeQuoteCurrency = tradeSymbol.split("_")[1];
        String hedgeQuoteCurrency = hedgeSymbol.split("_")[1];
        return getPriceRecursion(exchangeRateMap, hedgeQuoteCurrency, tradeQuoteCurrency);
    }


    private BigDecimal getPriceRecursion(Map<String, BigDecimal> priceMap, String baseCurrency, String quoteCurrency) {
        BigDecimal valuationPrice = getPriceBase(priceMap, baseCurrency, quoteCurrency);
        if (valuationPrice != null) {
            return valuationPrice;
        }

        List<String> midCurrencies = MarketConstant.MID_CURRENCIES;
        for (String midCurrency : midCurrencies) {
            BigDecimal baseMidPrice = getPriceBase(priceMap, baseCurrency, midCurrency);
            BigDecimal quoteMidPrice = getPriceBase(priceMap, quoteCurrency, midCurrency);
            if (baseMidPrice != null && quoteMidPrice != null) {
                return baseMidPrice.divide(quoteMidPrice, 18, RoundingMode.HALF_EVEN);
            }
        }

        return null;
    }

    private BigDecimal getPriceBase(Map<String, BigDecimal> priceMap, String baseCurrency, String quoteCurrency) {
        if (baseCurrency.equals(quoteCurrency)) {
            return BigDecimal.ONE;
        }

        String symbolName = baseCurrency + "_" + quoteCurrency;
        if (priceMap.containsKey(symbolName)) {
            return priceMap.get(symbolName);
        }

        String reverseSymbolName = quoteCurrency + "_" + baseCurrency;
        if ((priceMap.containsKey(reverseSymbolName))) {
            return BigDecimal.ONE.divide(priceMap.get(reverseSymbolName), 18, RoundingMode.HALF_EVEN);
        }

        return null;
    }

}
