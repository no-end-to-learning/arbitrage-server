package me.qiujun.arbitrage.enums;

public enum ConfigStatusEnum {

    ONLINE(true, true),
    BUY_ONLY(true, false),
    SELL_ONLY(false, true),
    SUSPEND(false, false),
    OFFLINE(false, false);

    private final boolean buy;
    private final boolean sell;

    ConfigStatusEnum(boolean buy, boolean sell) {
        this.buy = buy;
        this.sell = sell;
    }

    public boolean canTrade(OrderSideEnum side) {
        if (side == null) {
            return false;
        } else if (side == OrderSideEnum.BUY) {
            return buy;
        } else if (side == OrderSideEnum.SELL) {
            return sell;
        } else {
            return false;
        }
    }

}
