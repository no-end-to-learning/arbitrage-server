package me.qiujun.arbitrage.adapter.binance.enums;

import me.qiujun.arbitrage.enums.OrderSideEnum;

public enum BinanceOrderSideEnum {

    BUY(OrderSideEnum.BUY),
    SELL(OrderSideEnum.SELL);

    private final OrderSideEnum transformTo;

    BinanceOrderSideEnum(OrderSideEnum transformTo) {
        this.transformTo = transformTo;
    }

    public OrderSideEnum getTransformTo() {
        return transformTo;
    }

    public static BinanceOrderSideEnum valueOf(OrderSideEnum side) {
        return BinanceOrderSideEnum.valueOf(side.name());
    }

}
