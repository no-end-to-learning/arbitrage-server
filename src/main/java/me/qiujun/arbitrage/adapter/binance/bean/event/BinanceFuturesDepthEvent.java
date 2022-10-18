package me.qiujun.arbitrage.adapter.binance.bean.event;

import com.alibaba.fastjson.annotation.JSONField;
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
public class BinanceFuturesDepthEvent {

    @JSONField(name = "e")
    private String eventType;

    @JSONField(name = "E")
    private Long eventTime;

    @JSONField(name = "s")
    private String symbol;

    @JSONField(name = "U")
    private Long firstUpdateId;

    @JSONField(name = "u")
    private Long finalUpdateId;

    @JSONField(name = "b")
    private List<MarketOrderBookEntry> bids;

    @JSONField(name = "a")
    private List<MarketOrderBookEntry> asks;

}
