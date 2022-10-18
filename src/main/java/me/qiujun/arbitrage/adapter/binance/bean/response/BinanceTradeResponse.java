package me.qiujun.arbitrage.adapter.binance.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.adapter.binance.enums.BinanceOrderSideEnum;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceTradeResponse {

    // 交易ID
    private Long id;

    // 订单编号
    private Long orderId;

    // 交易对
    private String symbol;

    // 买卖方向
    private BinanceOrderSideEnum side;

    // 成交价
    private BigDecimal price;

    // 成交量
    private BigDecimal qty;

    // 成交额
    private BigDecimal quoteQty;

    // 实现盈亏
    private BigDecimal realizedPnl;

    // 手续费
    private BigDecimal commission;

    // 手续费计价单位
    private String commissionAsset;

    // 是否是买方
    private Boolean buyer;

    // 是否是挂单方
    private Boolean maker;

    // 持仓方向
    private String positionSide;

    // 时间
    private Long time;

}
