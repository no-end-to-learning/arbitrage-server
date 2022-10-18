package me.qiujun.arbitrage.task.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesHttpClient;
import me.qiujun.arbitrage.adapter.binance.BinanceProperties;
import me.qiujun.arbitrage.bean.base.Asset;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.AssetService;
import me.qiujun.arbitrage.service.MarketPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.binance.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BinanceFuturesAssetWatchTask {

    @Autowired
    private BinanceProperties binanceProperties;

    @Autowired
    private BinanceFuturesHttpClient binanceFuturesHttpClient;

    @Autowired
    private AssetService assetService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Scheduled(fixedRate = 500)
    public void execute() {
        List<Asset> assets = binanceFuturesHttpClient.listBalances().stream()
                .map(item -> {
                    BigDecimal total = item.getBalance().add(item.getCrossUnPnl());
                    BigDecimal available = item.getAvailableBalance();
                    BigDecimal freeze = total.subtract(available);

                    BigDecimal statTotal = BigDecimal.ZERO;
                    if (total.compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal valuationPrice = marketPriceService.getValuationPrice(item.getAsset());
                        statTotal = valuationPrice == null ? null : total.multiply(valuationPrice);
                    }

                    return Asset.builder()
                            .exchange(ExchangeEnum.BINANCE)
                            .exchangeMarket(ExchangeMarketEnum.FUTURES)
                            .exchangeAccount(binanceProperties.getAccount())
                            .currency(item.getAsset())
                            .available(available)
                            .freeze(freeze)
                            .total(total)
                            .statTotal(statTotal)
                            .snapshot(item.getBalance().compareTo(BigDecimal.ZERO) > 0)
                            .build();
                })
                .collect(Collectors.toList());
        assetService.set(ExchangeEnum.BINANCE, ExchangeMarketEnum.FUTURES, assets);
    }

}
