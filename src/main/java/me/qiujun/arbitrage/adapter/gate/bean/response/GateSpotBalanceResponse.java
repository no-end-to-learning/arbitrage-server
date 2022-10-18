package me.qiujun.arbitrage.adapter.gate.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateSpotBalanceResponse {

    private String currency;

    private BigDecimal available;

    private BigDecimal locked;

}
