package me.qiujun.arbitrage.task.bybit;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.bybit.BybitProperties;
import me.qiujun.arbitrage.adapter.bybit.BybitSpotHttpClient;
import me.qiujun.arbitrage.adapter.bybit.BybitSpotWebSocketClient;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitAccountEvent;
import me.qiujun.arbitrage.bean.base.Asset;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.event.CurrentConfigsChangeEvent;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.AssetService;
import me.qiujun.arbitrage.service.MarketPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.bybit.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BybitSpotAssetWatchTask {

    @Autowired
    private BybitProperties bybitProperties;

    @Autowired
    private BybitSpotHttpClient bybitSpotHttpClient;

    @Autowired
    private BybitSpotWebSocketClient bybitSpotWebSocketClient;

    @Autowired
    private AssetService assetService;

    @Autowired
    private MarketPriceService marketPriceService;

    private Closeable connection = null;

    @Async
    @EventListener
    public void onCurrentConfigsChangeEvent(CurrentConfigsChangeEvent event) {
        List<Config> configs = event.getNewConfigs().stream()
                .filter(item -> ExchangeEnum.BYBIT == item.getTradeExchange())
                .filter(item -> ExchangeMarketEnum.SPOT == item.getTradeExchangeMarket())
                .toList();

        // 如果没有相关币对则不建立 WebSocket 连接
        if (configs.isEmpty() && connection != null) {
            try {
                connection.close();
                log.info("connection manual closed due to configs change");
            } catch (IOException e) {
                log.error("connection manual close error", e);
            }
            return;
        }

        connection = bybitSpotWebSocketClient.onAccountEvent((msg) -> {
            Map<String, Asset> assetMap = new HashMap<>();
            List<BybitAccountEvent> accountEvents = msg.getData();
            for (BybitAccountEvent accountEvent : accountEvents) {
                for (BybitAccountEvent.Item item : accountEvent.getItems()) {
                    BigDecimal statTotal = BigDecimal.ZERO;
                    BigDecimal total = item.getFree().add(item.getLocked());
                    if (total.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal valuationPrice = marketPriceService.getValuationPrice(item.getCoin());
                        statTotal = valuationPrice == null ? null : total.multiply(valuationPrice);
                    }

                    Asset asset = Asset.builder()
                            .exchange(ExchangeEnum.BYBIT)
                            .exchangeMarket(ExchangeMarketEnum.SPOT)
                            .exchangeAccount(bybitProperties.getAccount())
                            .currency(item.getCoin())
                            .available(item.getFree())
                            .freeze(item.getLocked())
                            .total(total)
                            .statTotal(statTotal)
                            .snapshot(total.compareTo(BigDecimal.ZERO) > 0)
                            .build();
                    assetMap.put(item.getCoin(), asset);
                }
            }
            updateAssets(assetMap, false);
        });
    }

    @Scheduled(fixedRate = 15 * 1000)
    public void execute() {
        Map<String, Asset> assetMap = bybitSpotHttpClient.listBalances().stream()
                .map(item -> {
                    BigDecimal statTotal = BigDecimal.ZERO;
                    if (item.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal valuationPrice = marketPriceService.getValuationPrice(item.getCoin());
                        statTotal = valuationPrice == null ? null : item.getTotal().multiply(valuationPrice);
                    }

                    return Asset.builder()
                            .exchange(ExchangeEnum.BYBIT)
                            .exchangeMarket(ExchangeMarketEnum.SPOT)
                            .exchangeAccount(bybitProperties.getAccount())
                            .currency(item.getCoin())
                            .available(item.getFree())
                            .freeze(item.getLocked())
                            .total(item.getTotal())
                            .statTotal(statTotal)
                            .snapshot(item.getTotal().compareTo(BigDecimal.ZERO) > 0)
                            .build();
                })
                .collect(Collectors.toMap(Asset::getCurrency, item -> item));
        updateAssets(assetMap, true);
    }

    private void updateAssets(Map<String, Asset> updatedAssetMap, Boolean isOtherZero) {
        Map<String, Asset> existedAssetMap = assetService.get(ExchangeEnum.BYBIT, ExchangeMarketEnum.SPOT);
        for (String currency : existedAssetMap.keySet()) {
            if (updatedAssetMap.containsKey(currency)) {
                continue;
            }

            Asset existedAsset = existedAssetMap.get(currency);
            if (isOtherZero) {
                existedAsset.setAvailable(BigDecimal.ZERO);
                existedAsset.setFreeze(BigDecimal.ZERO);
                existedAsset.setTotal(BigDecimal.ZERO);
                existedAsset.setStatTotal(BigDecimal.ZERO);
            }
            updatedAssetMap.put(currency, existedAsset);
        }

        List<Asset> assets = new ArrayList<>(updatedAssetMap.values());
        assetService.set(ExchangeEnum.BYBIT, ExchangeMarketEnum.SPOT, assets);
    }
}
