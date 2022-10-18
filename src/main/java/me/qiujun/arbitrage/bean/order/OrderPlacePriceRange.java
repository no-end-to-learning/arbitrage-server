package me.qiujun.arbitrage.bean.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacePriceRange {

    private BigDecimal bestSpread;

    private BigDecimal bestPrice;

    private BigDecimal placeSpread;

    private BigDecimal placePrice;

    private BigDecimal worstSpread;

    private BigDecimal worstPrice;

}