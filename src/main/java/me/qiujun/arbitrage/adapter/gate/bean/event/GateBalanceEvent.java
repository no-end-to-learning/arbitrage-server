package me.qiujun.arbitrage.adapter.gate.bean.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateBalanceEvent {

    private Long timestamp;

    private Long timestampMs;

    private String user;

    private String currency;

    private BigDecimal change;

    private BigDecimal total;

    private BigDecimal available;

}
