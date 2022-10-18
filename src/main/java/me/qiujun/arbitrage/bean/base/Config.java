package me.qiujun.arbitrage.bean.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.enums.ConfigStatusEnum;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Config {

    private Long id;

    private Long serverId;

    private ExchangeEnum tradeExchange;

    private ExchangeMarketEnum tradeExchangeMarket;

    private String tradeSymbol;

    private Long tradeInterval;

    private Long tradeIntervalThreshold;

    private ExchangeEnum hedgeExchange;

    private ExchangeMarketEnum hedgeExchangeMarket;

    private String hedgeSymbol;

    private BigDecimal hedgeScale;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal minBaseSpread;

    private BigDecimal maxBaseSpread;

    private BigDecimal floatSpreadScale;

    private ConfigStatusEnum status;

    private Date createdAt;

    private Date updatedAt;

}