package me.qiujun.arbitrage.adapter.binance.enums;

import me.qiujun.arbitrage.enums.OrderTypeEnum;

public enum BinanceOrderTypeEnum {

    LIMIT(OrderTypeEnum.LIMIT),
    MARKET(OrderTypeEnum.MARKET),
    STOP_LOSS(null),
    STOP_LOSS_LIMIT(null),
    TAKE_PROFIT(null),
    TAKE_PROFIT_LIMIT(null),
    LIMIT_MAKER(null);

    private final OrderTypeEnum transformTo;

    BinanceOrderTypeEnum(OrderTypeEnum transformTo) {
        this.transformTo = transformTo;
    }

    public OrderTypeEnum getTransformTo() {
        return transformTo;
    }

    public static BinanceOrderTypeEnum valueOf(OrderTypeEnum type) {
        return BinanceOrderTypeEnum.valueOf(type.name());
    }

}
