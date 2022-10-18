package me.qiujun.arbitrage.bean.order;

import lombok.Data;
import me.qiujun.arbitrage.bean.market.MarketDepth;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.LinkedList;

@Data
public class OrderPlaceContext {

    private Long configId;

    private MarketDepth lastHedgeDepth = null;

    private Deque<BigDecimal> HistorySpreads = new LinkedList<>();

    public OrderPlaceContext(Long configId) {
        this.configId = configId;
    }

}
