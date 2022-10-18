package me.qiujun.arbitrage.adapter.gate.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.bean.base.SymbolConfig;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateSpotSymbolResponse {

    private String id;

    private String base;

    private String quote;

    private BigDecimal fee;

    private BigDecimal minBaseAmount;

    private BigDecimal minQuoteAmount;

    private Integer amountPrecision;

    private Integer precision;

    private String tradeStatus;

    private Long sellStart;

    private Long buyStart;

    public SymbolConfig transformTo() {
        return SymbolConfig.builder()
                .symbol(id)
                .baseCurrency(base)
                .quoteCurrency(quote)
                .pricePrecision(precision)
                .amountPrecision(amountPrecision)
                .baseCurrencyPrecision(amountPrecision)
                .minOrderAmount(minBaseAmount)
                .minOrderVolume(minQuoteAmount)
                .build();
    }

}
