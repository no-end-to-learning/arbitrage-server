package me.qiujun.arbitrage.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OrderStatusEnum {

    SUBMITTED(false, false, "已提交"),
    PROCESSING(false, false, "撮合中"),
    CANCELING(false, false, "取消中，此状态不入库，只给前端展示"),
    PARTIAL_FILLED(false, true, "部分成交"),
    PARTIAL_CANCELED(true, true, "部分成交撤销"),
    PARTIAL_REJECTED(true, true, "部分成交拒绝"),
    FILLED(true, true, "全部成交"),
    CANCELED(true, false, "撤销"),
    REJECTED(true, false, "拒绝");

    private final boolean isFinal;
    private final boolean hasFilled;
    private final String description;

    OrderStatusEnum(boolean isFinal, boolean hasFilled, String description) {
        this.isFinal = isFinal;
        this.hasFilled = hasFilled;
        this.description = description;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean hasFilled() {
        return hasFilled;
    }

    public String getDescription() {
        return description;
    }

    public static List<OrderStatusEnum> getProcessing() {
        return Arrays.stream(OrderStatusEnum.values())
                .filter(status -> !status.isFinal())
                .collect(Collectors.toList());
    }

    public static List<OrderStatusEnum> getFinished() {
        return Arrays.stream(OrderStatusEnum.values())
                .filter(OrderStatusEnum::isFinal)
                .collect(Collectors.toList());
    }

    public static OrderStatusEnum fromString(String text) {
        try {
            return OrderStatusEnum.valueOf(text.trim());
        } catch (Exception ex) {
            return null;
        }
    }

}
