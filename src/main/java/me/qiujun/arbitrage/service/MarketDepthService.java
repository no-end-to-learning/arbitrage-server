package me.qiujun.arbitrage.service;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.market.MarketDepth;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MarketDepthService {

    private final Map<String, MarketDepth> depthTable = new ConcurrentHashMap<>();

    private final Long DEPTH_TTL = 60 * 1000L;

    public void set(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket, String symbol, MarketDepth marketDepth) {
        depthTable.put(getMapKey(exchange, exchangeMarket, symbol), marketDepth);
    }

    public MarketDepth get(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket, String symbol) {
        MarketDepth marketDepth = depthTable.get(getMapKey(exchange, exchangeMarket, symbol));
        if (marketDepth == null) {
            return null;
        } else if (System.currentTimeMillis() - marketDepth.getUpdatedAt() > DEPTH_TTL) {
            log.info("depth of {} {} {} has expired", exchange, exchangeMarket, symbol);
            return null;
        }

        return marketDepth;
    }

    public MarketDepth getFromTradeConfig(Config config) {
        return get(config.getTradeExchange(), config.getTradeExchangeMarket(), config.getTradeSymbol());
    }

    public MarketDepth getFromHedgeConfig(Config config) {
        return get(config.getHedgeExchange(), config.getHedgeExchangeMarket(), config.getHedgeSymbol());
    }

    private String getMapKey(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket, String symbol) {
        return String.format("%s#%s#%s", exchange.name(), exchangeMarket.name(), symbol);
    }

}
