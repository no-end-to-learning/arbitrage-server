package me.qiujun.arbitrage.task.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesHttpClient;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceExchangeInfoResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceExchangeSymbolFilterResponse;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceExchangeSymbolFilterTypeEnum;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceExchangeSymbolResponse;
import me.qiujun.arbitrage.bean.base.SymbolConfig;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.MarketSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.binance.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BinanceFuturesSymbolSyncTask {

    @Autowired
    private BinanceFuturesHttpClient futuresHttpClient;

    @Autowired
    private MarketSymbolService marketSymbolService;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void execute() {
        List<SymbolConfig> symbolConfigs = new ArrayList<>();
        BinanceExchangeInfoResponse binanceExchangeInfoResponse = futuresHttpClient.getExchangeInfo();
        List<BinanceExchangeSymbolResponse> symbols = binanceExchangeInfoResponse.getSymbols();
        for (BinanceExchangeSymbolResponse item : symbols) {
            SymbolConfig symbolConfig = SymbolConfig.builder()
                    .symbol(String.format("%s_%s", item.getBaseAsset(), item.getQuoteAsset()))
                    .baseCurrency(item.getBaseAsset())
                    .quoteCurrency(item.getQuoteAsset())
                    .pricePrecision(item.getPricePrecision())
                    .amountPrecision(item.getQuantityPrecision())
                    .baseCurrencyPrecision(item.getBaseAssetPrecision())
                    .quoteCurrencyPrecision(item.getQuotePrecision())
                    .build();

            for (BinanceExchangeSymbolFilterResponse filter : item.getFilters()) {
                if (filter.getFilterType() == BinanceExchangeSymbolFilterTypeEnum.MARKET_LOT_SIZE) {
                    symbolConfig.setMinOrderAmount(new BigDecimal(filter.getMinQty()));
                }
            }

            symbolConfigs.add(symbolConfig);
        }
        marketSymbolService.setConfigs(ExchangeEnum.BINANCE, ExchangeMarketEnum.FUTURES, symbolConfigs);
    }

}
