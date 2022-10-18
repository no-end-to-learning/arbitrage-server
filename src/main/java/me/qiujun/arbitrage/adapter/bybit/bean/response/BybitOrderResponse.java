package me.qiujun.arbitrage.adapter.bybit.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.enums.*;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitOrderResponse {

    private String accountId;

    private String symbol;

    private String orderLinkId;

    private String orderId;

    private BigDecimal orderPrice;

    private BigDecimal orderQty;

    private BigDecimal execQty;

    private BigDecimal cummulativeQuoteQty;

    private BigDecimal avgPrice;

    private String status;

    private String timeInForce;

    private String orderType;

    private String side;

    private BigDecimal stopPrice;

    private Long createTime;

    private Long updateTime;

    private String isWorking;

    private String locked;

    public OrderStatusEnum getTransformedStatus() {
        switch (this.getStatus()) {
            case "NEW" -> {
                return OrderStatusEnum.PROCESSING;
            }
            case "PARTIALLY_FILLED" -> {
                return OrderStatusEnum.PARTIAL_FILLED;
            }
            case "FILLED" -> {
                return OrderStatusEnum.FILLED;
            }
            case "CANCELED" -> {
                if (this.getExecQty().compareTo(BigDecimal.ZERO) > 0) {
                    return OrderStatusEnum.PARTIAL_CANCELED;
                } else {
                    return OrderStatusEnum.CANCELED;
                }
            }
            case "PARTIALLY_FILLED_CANCELED" -> {
                return OrderStatusEnum.PARTIAL_CANCELED;
            }
            case "REJECTED" -> {
                if (this.getExecQty().compareTo(BigDecimal.ZERO) > 0) {
                    return OrderStatusEnum.PARTIAL_REJECTED;
                } else {
                    return OrderStatusEnum.REJECTED;
                }
            }
            default -> {
                log.error("order {} status {} unknown", this.getOrderId(), this.getStatus());
                return OrderStatusEnum.PROCESSING;
            }
        }
    }

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
        OrderStatusEnum orderStatus = getTransformedStatus();

        BigDecimal quoteAmountFilled = BigDecimal.ZERO;
        if (this.getExecQty().compareTo(BigDecimal.ZERO) > 0) {
            quoteAmountFilled = this.getExecQty().multiply(this.getAvgPrice());
        }

        return Order.builder()
                .id(order.getId())
                .configId(order.getConfigId())
                .tradeType(order.getTradeType())
                .exchange(ExchangeEnum.BYBIT)
                .exchangeMarket(ExchangeMarketEnum.SPOT)
                .exchangeOrderId(this.getOrderId())
                .symbol(order.getSymbol())
                .side(OrderSideEnum.valueOf(this.getSide()))
                .type(OrderTypeEnum.valueOf(this.getOrderType()))
                .price(this.getOrderPrice())
                .status(orderStatus)
                .baseCurrency(order.getBaseCurrency())
                .baseAmount(this.getOrderQty())
                .baseAmountFilled(this.getExecQty())
                .quoteCurrency(order.getQuoteCurrency())
                .quoteAmount(this.getOrderQty().multiply(this.getOrderPrice()))
                .quoteAmountFilled(quoteAmountFilled)
                .feeCurrency(order.getFeeCurrency())
                .feeAmount(order.getFeeAmount())
                .spread(order.getSpread())
                .exchangeRate(order.getExchangeRate())
                .hedgeOrderId(order.getHedgeOrderId())
                .statIncome(order.getStatIncome())
                .statCost(order.getStatCost())
                .finishedAt(orderStatus.isFinal() ? new Date(this.getUpdateTime()) : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
