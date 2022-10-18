package me.qiujun.arbitrage.bean.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SymbolConfig {

    private String symbol;

    private String baseCurrency;

    private String quoteCurrency;

    private Integer pricePrecision;

    private Integer amountPrecision;

    private Integer volumePrecision;

    private Integer baseCurrencyPrecision;

    private Integer quoteCurrencyPrecision;

    private BigDecimal minOrderAmount;

    private BigDecimal minOrderVolume;

}
