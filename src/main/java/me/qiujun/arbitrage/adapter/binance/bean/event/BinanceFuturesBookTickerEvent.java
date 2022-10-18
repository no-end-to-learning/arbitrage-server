package me.qiujun.arbitrage.adapter.binance.bean.event;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceFuturesBookTickerEvent {

    @JSONField(name = "e")
    private String eventType;

    @JSONField(name = "E")
    private Long eventTime;

    @JSONField(name = "u")
    private Long updateId;

    @JSONField(name = "s")
    private String symbol;

    @JSONField(name = "b")
    private BigDecimal bidPrice;

    @JSONField(name = "B")
    private BigDecimal bidQuantity;

    @JSONField(name = "a")
    private BigDecimal askPrice;

    @JSONField(name = "A")
    private BigDecimal askQuantity;

}