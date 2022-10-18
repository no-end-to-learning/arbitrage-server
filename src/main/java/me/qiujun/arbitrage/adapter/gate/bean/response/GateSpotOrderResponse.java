package me.qiujun.arbitrage.adapter.gate.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.constant.AssetConstant;
import me.qiujun.arbitrage.enums.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateSpotOrderResponse {

    private String id;

    private String text;

    private Long createTime;

    private Long createTimeMs;

    private Long updateTime;

    private Long updateTimeMs;

    private String status;

    private String currencyPair;

    private String type;

    private String account;

    private String side;

    private BigDecimal amount;

    private BigDecimal price;

    private String timeInForce;

    private String iceBerg;

    private String autoBorrow;

    private String autoRepay;

    private BigDecimal left;

    private BigDecimal filledTotal;

    private BigDecimal fee;

    private String feeCurrency;

    private BigDecimal pointFee;

    private BigDecimal gtFee;

    private Boolean gtDiscount;

    private BigDecimal rebatedFee;

    private String rebatedFeeCurrency;

    public OrderStatusEnum getTransformedStatus() {
        switch (status) {
            case "closed" -> {
                return OrderStatusEnum.FILLED;
            }
            case "cancelled" -> {
                if (filledTotal.compareTo(BigDecimal.ZERO) > 0) {
                    return OrderStatusEnum.PARTIAL_CANCELED;
                } else {
                    return OrderStatusEnum.CANCELED;
                }
            }
            default -> {
                if (filledTotal.compareTo(BigDecimal.ZERO) > 0) {
                    return OrderStatusEnum.PARTIAL_FILLED;
                } else {
                    return OrderStatusEnum.PROCESSING;
                }
            }
        }
    }

    public Order transformTo() {
        return transformTo(new Order());
    }

    public Order transformTo(Order order) {
        String[] symbolSplit = currencyPair.split("_");
        OrderStatusEnum orderStatus = getTransformedStatus();
        return Order.builder()
                .id(order.getId())
                .configId(order.getConfigId())
                .tradeType(order.getTradeType())
                .exchange(ExchangeEnum.GATE)
                .exchangeMarket(ExchangeMarketEnum.SPOT)
                .exchangeOrderId(id)
                .symbol(currencyPair)
                .side(OrderSideEnum.valueOf(side.toUpperCase()))
                .type(OrderTypeEnum.valueOf(type.toUpperCase()))
                .price(price)
                .status(orderStatus)
                .baseCurrency(symbolSplit[0])
                .baseAmount(amount)
                .baseAmountFilled(amount.subtract(left))
                .quoteCurrency(symbolSplit[1])
                .quoteAmount(amount.multiply(price))
                .quoteAmountFilled(filledTotal)
                .feeCurrency(fee.compareTo(BigDecimal.ZERO) > 0 ? feeCurrency : AssetConstant.GATE_POINT)
                .feeAmount(fee.compareTo(BigDecimal.ZERO) > 0 ? fee : pointFee)
                .spread(order.getSpread())
                .exchangeRate(order.getExchangeRate())
                .hedgeOrderId(order.getHedgeOrderId())
                .statIncome(order.getStatIncome())
                .statCost(order.getStatCost())
                .finishedAt(orderStatus.isFinal() ? new Date(updateTimeMs) : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
