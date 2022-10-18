package me.qiujun.arbitrage.bean.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketBookTicker {

    private Long id;

    private Long batchId;

    private ExchangeEnum exchange;

    private ExchangeMarketEnum exchangeMarket;

    private String symbol;

    private BigDecimal bidPrice;

    private BigDecimal bidAmount;

    private BigDecimal askPrice;

    private BigDecimal askAmount;

    private Date createdAt;

    private Date updatedAt;

}