package me.qiujun.arbitrage.adapter.gate.bean.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.constant.AssetConstant;
import me.qiujun.arbitrage.enums.*;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateOrderEvent {

    private String id;

    private String user;

    private String text;

    private Long createTime;

    private Long createTimeMs;

    private Long updateTime;

    private Long updateTimeMs;

    private String event;

    private String currencyPair;

    private String type;

    private String account;

    private String side;

    private BigDecimal amount;

    private BigDecimal price;

    private String timeInForce;

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
        switch (event) {
            case "put" -> {
                return OrderStatusEnum.PROCESSING;
            }
            case "update" -> {
                return OrderStatusEnum.PARTIAL_FILLED;
            }
            case "finish" -> {
                if (filledTotal.compareTo(BigDecimal.ZERO) > 0) {
                    if (left.compareTo(BigDecimal.ZERO) > 0) {
                        return OrderStatusEnum.PARTIAL_CANCELED;
                    } else {
                        return OrderStatusEnum.FILLED;
                    }
                } else {
                    return OrderStatusEnum.CANCELED;
                }
            }
            default -> {
                log.error("unknown event {}", event);
                return OrderStatusEnum.PROCESSING;
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
