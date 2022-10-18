package me.qiujun.arbitrage.service;

import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.base.SymbolConfig;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MarketSymbolService {

    private final Map<String, Map<String, SymbolConfig>> configTable = new ConcurrentHashMap<>();

    public void setConfigs(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket, List<SymbolConfig> configs) {
        Map<String, SymbolConfig> configMap = configs.stream()
                .collect(Collectors.toMap(SymbolConfig::getSymbol, config -> config, (config1, config2) -> config1));
        configTable.put(getMapKey(exchange, exchangeMarket), configMap);
    }

    public SymbolConfig getConfig(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket, String symbol) {
        Map<String, SymbolConfig> configMap = configTable.get(getMapKey(exchange, exchangeMarket));
        if (configMap == null) {
            return null;
        }
        return configMap.get(symbol);
    }

    public SymbolConfig getConfigFromTradeConfig(Config config) {
        return getConfig(config.getTradeExchange(), config.getTradeExchangeMarket(), config.getTradeSymbol());
    }

    public SymbolConfig getConfigFromHedgeConfig(Config config) {
        return getConfig(config.getHedgeExchange(), config.getHedgeExchangeMarket(), config.getHedgeSymbol());
    }

    private String getMapKey(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket) {
        return String.format("%s#%s", exchange.name(), exchangeMarket.name());
    }

}
