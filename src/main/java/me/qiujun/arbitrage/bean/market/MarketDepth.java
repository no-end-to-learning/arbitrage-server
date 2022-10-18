package me.qiujun.arbitrage.bean.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketDepth {

    private String id;

    private String symbol;

    private List<MarketOrderBookEntry> bids;

    private List<MarketOrderBookEntry> asks;

    private long updatedAt;

}
