package me.qiujun.arbitrage.task.binance;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.BinanceFuturesWebSocketClient;
import me.qiujun.arbitrage.adapter.binance.bean.event.BinanceFuturesBookTickerEvent;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.event.CurrentConfigsChangeEvent;
import me.qiujun.arbitrage.bean.market.MarketDepth;
import me.qiujun.arbitrage.bean.market.MarketOrderBookEntry;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.MarketDepthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.binance.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BinanceFuturesDepthWatchTask {

    @Autowired
    private BinanceFuturesWebSocketClient futuresWebSocketClient;

    @Autowired
    private MarketDepthService marketDepthService;

    private Closeable connection = null;

    @Async
    @EventListener
    public void onCurrentConfigsChangeEvent(CurrentConfigsChangeEvent event) {
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
        return configs.stream()
                .filter(item -> ExchangeEnum.BINANCE == item.getHedgeExchange())
                .filter(item -> ExchangeMarketEnum.FUTURES == item.getHedgeExchangeMarket())
                .collect(Collectors.toMap(
                        item -> item.getHedgeSymbol().replace("_", "").toLowerCase(),
                        Config::getHedgeSymbol,
                        (config1, config2) -> config1
                ));
    }

    private Closeable listen(Map<String, String> listenSymbolMap) {
        String listenSymbols = String.join(",", listenSymbolMap.keySet());
        log.info("listen binance futures depth symbols: {}", listenSymbols);

        return futuresWebSocketClient.onBookTickerEvent(listenSymbols, (stream) -> {
            BinanceFuturesBookTickerEvent bookTicker = stream.getData();
            MarketOrderBookEntry bid = new MarketOrderBookEntry(bookTicker.getBidPrice(), bookTicker.getBidQuantity());
            MarketOrderBookEntry ask = new MarketOrderBookEntry(bookTicker.getAskPrice(), bookTicker.getAskQuantity());
            MarketDepth marketDepth = MarketDepth.builder()
                    .id(bookTicker.getUpdateId().toString())
                    .symbol(listenSymbolMap.get(bookTicker.getSymbol().toLowerCase()))
                    .bids(Collections.singletonList(bid))
                    .asks(Collections.singletonList(ask))
                    .updatedAt(bookTicker.getEventTime())
                    .build();
            marketDepthService.set(ExchangeEnum.BINANCE, ExchangeMarketEnum.FUTURES, marketDepth.getSymbol(), marketDepth);
        });
    }

}
