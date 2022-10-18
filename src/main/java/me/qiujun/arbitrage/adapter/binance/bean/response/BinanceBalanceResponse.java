package me.qiujun.arbitrage.adapter.binance.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceBalanceResponse {

    private String accountAlias; // 账户唯一识别码

    private String asset; // 资产

    private BigDecimal balance; // 总余额

    private BigDecimal crossWalletBalance; // 全仓余额

    private BigDecimal crossUnPnl; // 全仓持仓未实现盈亏

    private BigDecimal availableBalance; // 下单可用余额

    private BigDecimal maxWithdrawAmount; // 最大可转出余额

    private Boolean marginAvailable; // 是否可用作联合保证金

    private Long updateTime; // 更新时间
}
