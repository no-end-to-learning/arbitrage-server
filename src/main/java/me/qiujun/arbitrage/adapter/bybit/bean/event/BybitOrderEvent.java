package me.qiujun.arbitrage.adapter.bybit.bean.event;

import com.alibaba.fastjson.annotation.JSONField;
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
public class BybitOrderEvent {

    @JSONField(name = "e")
    private String eventType;

    @JSONField(name = "E")
    private Long eventTime;

    @JSONField(name = "s")
    private String symbol;

    @JSONField(name = "c")
    private String orderLinkId;

    @JSONField(name = "S")
    private String side;

    @JSONField(name = "o")
    private String orderType;

    @JSONField(name = "f")
    private String timeInForce;

    @JSONField(name = "q")
    private BigDecimal orderQty;

    @JSONField(name = "p")
    private BigDecimal orderPrice;

    @JSONField(name = "X")
    private String status;

    @JSONField(name = "i")
    private String orderId;

    @JSONField(name = "z")
    private BigDecimal execQty;

    @JSONField(name = "Z")
    private BigDecimal execAmount;

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
                .quoteAmountFilled(this.getExecAmount())
                .feeCurrency("USDT")
                .feeAmount(BigDecimal.ZERO)
                .spread(order.getSpread())
                .exchangeRate(order.getExchangeRate())
                .hedgeOrderId(order.getHedgeOrderId())
                .statIncome(order.getStatIncome())
                .statCost(order.getStatCost())
                .finishedAt(orderStatus.isFinal() ? new Date(this.getEventTime()) : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
