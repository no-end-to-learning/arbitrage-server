package me.qiujun.arbitrage.adapter.gate.bean.event;

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
public class GateBookTickerEvent {

    @JSONField(name = "t")
    private Long timestamp;

    @JSONField(name = "u")
    private Long id;

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