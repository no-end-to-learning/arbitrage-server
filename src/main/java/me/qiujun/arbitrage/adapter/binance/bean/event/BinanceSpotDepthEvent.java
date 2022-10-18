package me.qiujun.arbitrage.adapter.binance.bean.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.bean.market.MarketOrderBookEntry;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceSpotDepthEvent {

    private Long lastUpdateId;

    private List<MarketOrderBookEntry> bids;

    private List<MarketOrderBookEntry> asks;

}
