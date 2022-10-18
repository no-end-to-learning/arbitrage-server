package me.qiujun.arbitrage.adapter.binance.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.adapter.binance.enums.BinanceOrderSideEnum;
import me.qiujun.arbitrage.adapter.binance.enums.BinanceOrderStatusEnum;
import me.qiujun.arbitrage.adapter.binance.enums.BinanceOrderTypeEnum;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceFuturesOrderResponse {

    // 交易对
    private String symbol;

    // 系统订单号
    private Long orderId;

    // 用户自定义的订单号
    private String clientOrderId;

    // 委托价格
    private BigDecimal price;

    // 原始委托数量
    private BigDecimal origQty;

    // 成交量
    private BigDecimal executedQty;

    // 订单状态
    private BinanceOrderStatusEnum status;

    // 订单类型
    private BinanceOrderTypeEnum type;

    // 买卖方向
    private BinanceOrderSideEnum side;

    // 成交金额
    private BigDecimal cumQuote;

    // 更新时间
    private Long updateTime;

    public Order transformTo(String symbol) {
        String[] symbolSplit = symbol.split("_");
        Order order = Order.builder()
                .symbol(symbol)
                .baseCurrency(symbolSplit[0])
                .quoteCurrency(symbolSplit[1])
                .build();
        return transformTo(order);
    }

    public Order transformTo(Order order) {
        return Order.builder()
                .id(order.getId())
                .configId(order.getConfigId())
                .tradeType(order.getTradeType())
                .exchange(ExchangeEnum.BINANCE)
                .exchangeMarket(ExchangeMarketEnum.FUTURES)
                .exchangeOrderId(this.getOrderId().toString())
                .symbol(order.getSymbol())
                .side(this.getSide().getTransformTo())
                .type(this.getType().getTransformTo())
                .price(this.getPrice())
                .status(this.getStatus().getTransformTo())
                .baseCurrency(order.getBaseCurrency())
                .baseAmount(this.getOrigQty())
                .baseAmountFilled(this.getExecutedQty())
                .quoteCurrency(order.getQuoteCurrency())
                .quoteAmount(order.getQuoteAmount())
                .quoteAmountFilled(this.getCumQuote())
                .feeCurrency(order.getFeeCurrency())
                .feeAmount(order.getFeeAmount())
                .spread(order.getSpread())
                .exchangeRate(order.getExchangeRate())
                .hedgeOrderId(order.getHedgeOrderId())
                .statIncome(order.getStatIncome())
                .statCost(order.getStatCost())
                .finishedAt(this.getStatus().getTransformTo().isFinal() ? new Date(updateTime) : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
