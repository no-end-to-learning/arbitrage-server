package me.qiujun.arbitrage.adapter.binance.enums;

import me.qiujun.arbitrage.enums.OrderStatusEnum;

public enum BinanceOrderStatusEnum {

    NEW(OrderStatusEnum.SUBMITTED),
    PARTIALLY_FILLED(OrderStatusEnum.PARTIAL_FILLED),
    FILLED(OrderStatusEnum.FILLED),
    CANCELED(OrderStatusEnum.CANCELED),
    PENDING_CANCEL(OrderStatusEnum.CANCELING),
    REJECTED(OrderStatusEnum.REJECTED),
    EXPIRED(OrderStatusEnum.CANCELED);

    private final OrderStatusEnum transformTo;

    BinanceOrderStatusEnum(OrderStatusEnum transformTo) {
        this.transformTo = transformTo;
    }

    public OrderStatusEnum getTransformTo() {
        return transformTo;
    }

}
