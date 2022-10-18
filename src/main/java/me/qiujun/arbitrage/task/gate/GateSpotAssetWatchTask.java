package me.qiujun.arbitrage.task.gate;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.gate.GateProperties;
import me.qiujun.arbitrage.adapter.gate.GateSpotHttpClient;
import me.qiujun.arbitrage.adapter.gate.GateSpotWebSocketClient;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateBalanceEvent;
import me.qiujun.arbitrage.bean.base.Asset;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.event.CurrentConfigsChangeEvent;
import me.qiujun.arbitrage.constant.AssetConstant;
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
        value = "app.gate.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class GateSpotAssetWatchTask {

    @Autowired
    private GateProperties gateProperties;

    @Autowired
    private GateSpotHttpClient gateSpotHttpClient;

    @Autowired
    private GateSpotWebSocketClient gateSpotWebSocketClient;

    @Autowired
    private AssetService assetService;

    @Autowired
    private MarketPriceService marketPriceService;

    private Closeable connection = null;

    @Async
    @EventListener
    public void onCurrentConfigsChangeEvent(CurrentConfigsChangeEvent event) {
        List<Config> configs = event.getNewConfigs().stream()
                .filter(item -> ExchangeEnum.GATE == item.getTradeExchange())
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

        connection = gateSpotWebSocketClient.onBalanceEvent((msg) -> {
            Map<String, Asset> updatedAssetMap = new HashMap<>();
            for (GateBalanceEvent item : msg.getResult()) {
                BigDecimal statTotal = BigDecimal.ZERO;
                if (item.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal valuationPrice = marketPriceService.getValuationPrice(item.getCurrency());
                    if (item.getCurrency().equals(AssetConstant.GATE_POINT)) {
                        valuationPrice = AssetConstant.GATE_POINT_PRICE;
                    }
                    statTotal = valuationPrice == null ? null : item.getTotal().multiply(valuationPrice);
                }

                Asset asset = Asset.builder()
                        .exchange(ExchangeEnum.GATE)
                        .exchangeMarket(ExchangeMarketEnum.SPOT)
                        .exchangeAccount(gateProperties.getAccount())
                        .currency(item.getCurrency())
                        .available(item.getAvailable())
                        .freeze(item.getTotal().subtract(item.getAvailable()))
                        .total(item.getTotal())
                        .statTotal(statTotal)
                        .snapshot(item.getTotal().compareTo(BigDecimal.ZERO) > 0)
                        .build();
                updatedAssetMap.put(item.getCurrency(), asset);
            }

            Map<String, Asset> existedAssetMap = assetService.get(ExchangeEnum.GATE, ExchangeMarketEnum.SPOT);
            for (String currency : existedAssetMap.keySet()) {
                if (updatedAssetMap.containsKey(currency)) {
                    continue;
                }

                updatedAssetMap.put(currency, existedAssetMap.get(currency));
            }

            List<Asset> assets = new ArrayList<>(updatedAssetMap.values());
            assetService.set(ExchangeEnum.GATE, ExchangeMarketEnum.SPOT, assets);
        });
    }

    @Scheduled(fixedRate = 15 * 1000)
    public void execute() {
        List<Asset> assets = gateSpotHttpClient.listBalances().stream()
                .map(item -> {
                    BigDecimal total = item.getAvailable().add(item.getLocked());

                    BigDecimal statTotal = BigDecimal.ZERO;
                    if (total.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal valuationPrice = marketPriceService.getValuationPrice(item.getCurrency());
                        if (item.getCurrency().equals(AssetConstant.GATE_POINT)) {
                            valuationPrice = AssetConstant.GATE_POINT_PRICE;
                        }
                        statTotal = valuationPrice == null ? null : total.multiply(valuationPrice);
                    }

                    return Asset.builder()
                            .exchange(ExchangeEnum.GATE)
                            .exchangeMarket(ExchangeMarketEnum.SPOT)
                            .exchangeAccount(gateProperties.getAccount())
                            .currency(item.getCurrency())
                            .available(item.getAvailable())
                            .freeze(item.getLocked())
                            .total(total)
                            .statTotal(statTotal)
                            .snapshot(total.compareTo(BigDecimal.ZERO) > 0)
                            .build();
                })
                .collect(Collectors.toList());
        assetService.set(ExchangeEnum.GATE, ExchangeMarketEnum.SPOT, assets);
    }

}
