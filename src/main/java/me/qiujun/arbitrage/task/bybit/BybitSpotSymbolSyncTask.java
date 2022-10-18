package me.qiujun.arbitrage.task.bybit;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.bybit.BybitSpotHttpClient;
import me.qiujun.arbitrage.bean.base.SymbolConfig;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.MarketSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.bybit.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BybitSpotSymbolSyncTask {

    @Autowired
    private BybitSpotHttpClient bybitSpotHttpClient;

    @Autowired
    private MarketSymbolService marketSymbolService;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void execute() {
        List<SymbolConfig> symbolConfigs = bybitSpotHttpClient.listSymbols().stream()
                .map(item -> {
                    return SymbolConfig.builder()
                            .symbol(String.format("%s_%s", item.getBaseCoin(), item.getQuoteCoin()))
                            .baseCurrency(item.getBaseCoin())
                            .quoteCurrency(item.getQuoteCoin())
                            .pricePrecision(item.getMinPricePrecision().stripTrailingZeros().scale())
                            .amountPrecision(item.getBasePrecision().stripTrailingZeros().scale())
                            .volumePrecision(item.getQuotePrecision().stripTrailingZeros().scale())
                            .baseCurrencyPrecision(item.getBasePrecision().stripTrailingZeros().scale())
                            .quoteCurrencyPrecision(item.getQuotePrecision().stripTrailingZeros().scale())
                            .minOrderAmount(item.getMinTradeQty())
                            .minOrderVolume(item.getMinTradeAmt())
                            .build();
                })
                .collect(Collectors.toList());
        marketSymbolService.setConfigs(ExchangeEnum.BYBIT, ExchangeMarketEnum.SPOT, symbolConfigs);
    }

}
