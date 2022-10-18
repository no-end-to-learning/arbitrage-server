package me.qiujun.arbitrage.task.common;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceSpotWebSocketClient;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceSpotDepthEvent;
import me.qiujun.arbitrage.bean.base.Asset;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.event.AnyConfigsChangeEvent;
import me.qiujun.arbitrage.config.SystemProperties;
import me.qiujun.arbitrage.constant.MarketConstant;
import me.qiujun.arbitrage.service.AssetService;
import me.qiujun.arbitrage.service.MarketPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Component
public class CommonMarketPriceListenTask {

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private BinanceSpotWebSocketClient spotWebSocketClient;

    @Autowired
    private MarketPriceService marketPriceService;

    @Autowired
    private AssetService assetService;

    private Closeable connection = null;

    @Async
    @EventListener
    public void onAnyConfigsChangeEvent(AnyConfigsChangeEvent event) {
        // 连接已建立则关闭连接
        if (connection != null) {
            try {
                connection.close();
                log.info("connection manual closed due to configs change");
            } catch (IOException e) {
                log.error("connection manual close error", e);
            }
        }

        Map<String, String> listenSymbolMap = getListenSymbolMap(event.getNewConfigs());
        if (!listenSymbolMap.isEmpty()) {
            connection = listen(listenSymbolMap);
        }
    }

    private Map<String, String> getListenSymbolMap(List<Config> configs) {
        Set<String> symbols = new HashSet<>();

        // 当前服务器跑的交易所
        List<String> configExchangeMarkets = new ArrayList<>();
        for (Config config : configs) {
            if (!config.getServerId().equals(systemProperties.getServerId())) {
                continue;
            }

            configExchangeMarkets.add(config.getTradeExchange() + "#" + config.getTradeExchangeMarket());
            configExchangeMarkets.add(config.getHedgeExchange() + "#" + config.getHedgeExchangeMarket());
        }

        // 交易所的配置
        for (Config config : configs) {
            String tradeExchangeMarket = config.getTradeExchange() + "#" + config.getTradeExchangeMarket();
            if (configExchangeMarkets.contains(tradeExchangeMarket)) {
                String[] symbolSplit = config.getTradeSymbol().split("_");
                symbols.add(symbolSplit[0] + "_" + MarketConstant.DEFAULT_CURRENCY);
                symbols.add(symbolSplit[1] + "_" + MarketConstant.DEFAULT_CURRENCY);
            }

            String hedgeExchangeMarket = config.getHedgeExchange() + "#" + config.getHedgeExchangeMarket();
            if (configExchangeMarkets.contains(hedgeExchangeMarket)) {
                String[] symbolSplit = config.getHedgeSymbol().split("_");
                if (config.getHedgeScale().compareTo(BigDecimal.ONE) == 0) {
                    symbols.add(symbolSplit[0] + "_" + MarketConstant.DEFAULT_CURRENCY);
                } else {
                    String hedgeScale = String.valueOf(config.getHedgeScale().intValue());
                    String hedgeBaseCurrency = symbolSplit[0].replace(hedgeScale, "");
                    symbols.add(hedgeBaseCurrency + "_" + MarketConstant.DEFAULT_CURRENCY);
                }
                symbols.add(symbolSplit[1] + "_" + MarketConstant.DEFAULT_CURRENCY);
            }
        }

        // 有资产的币对
        List<Asset> assets = assetService.listNeedSnapshot();
        for (Asset asset : assets) {
            if (asset.getTotal().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            String assetExchangeMarket = asset.getExchange() + "#" + asset.getExchangeMarket();
            if (configExchangeMarkets.contains(assetExchangeMarket)) {
                symbols.add(asset.getCurrency() + "_" + MarketConstant.DEFAULT_CURRENCY);
            }
        }

        // 处理特殊币对
        symbols.addAll(Arrays.asList("BUSD_USDT", "BNB_USDT"));

        Map<String, String> symbolMap = new HashMap<>();
        for (String symbol : symbols) {
            String[] symbolSplit = symbol.split("_");
            if (symbolSplit[0].equals(symbolSplit[1])) {
                continue;
            }
            symbolMap.put(symbolSplit[0] + symbolSplit[1], symbolSplit[0] + "_" + symbolSplit[1]);
        }

        return symbolMap;
    }

    private Closeable listen(Map<String, String> listenSymbolMap) {
        String listenSymbols = String.join(",", listenSymbolMap.keySet());
        log.info("listen market price symbols: {}", listenSymbols);

        return spotWebSocketClient.onDepthEvent(listenSymbols, (stream) -> {
            String streamName = stream.getStream();
            String binanceSymbol = streamName.split("@")[0].toUpperCase();
            String symbol = listenSymbolMap.get(binanceSymbol);

            BinanceSpotDepthEvent depth = stream.getData();
            BigDecimal askPrice = depth.getAsks().get(0).getPrice();
            BigDecimal bidPrice = depth.getBids().get(0).getPrice();

            BigDecimal midPrice = askPrice.add(bidPrice).divide(BigDecimal.valueOf(2), 18, RoundingMode.HALF_EVEN);
            marketPriceService.setMarketPrice(symbol, midPrice);
        });
    }

}
