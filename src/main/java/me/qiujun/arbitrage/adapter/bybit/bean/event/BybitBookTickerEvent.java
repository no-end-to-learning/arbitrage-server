package me.qiujun.arbitrage.adapter.bybit.bean.event;

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
public class BybitBookTickerEvent {

    @JSONField(name = "s")
    private String symbol;

    @JSONField(name = "bp")
    private BigDecimal bidPrice;

    @JSONField(name = "bq")
    private BigDecimal bidQuantity;

    @JSONField(name = "ap")
    private BigDecimal askPrice;

    @JSONField(name = "aq")
    private BigDecimal askQuantity;

    @JSONField(name = "t")
    private Long createTime;

    @JSONField(name = "ts")
    private Long pushTime;

}