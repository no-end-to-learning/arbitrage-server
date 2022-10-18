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
@NoArgsConstructor
@AllArgsConstructor
public class AssetSnapshot {

    private Long id;

    private Long serverId;

    private Long batchId;

    private ExchangeEnum exchange;

    private ExchangeMarketEnum exchangeMarket;

    private String exchangeAccount;

    private String currency;

    private BigDecimal available;

    private BigDecimal freeze;

    private BigDecimal total;

    private BigDecimal statTotal;

    private Date createdAt;

    private Date updatedAt;

}