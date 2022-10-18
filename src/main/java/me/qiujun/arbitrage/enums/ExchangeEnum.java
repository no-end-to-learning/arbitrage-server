package me.qiujun.arbitrage.enums;

public enum ExchangeEnum {

    BINANCE("Binance"),
    GATE("Gate"),
    BYBIT("Bybit");

    private final String name;

    ExchangeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
