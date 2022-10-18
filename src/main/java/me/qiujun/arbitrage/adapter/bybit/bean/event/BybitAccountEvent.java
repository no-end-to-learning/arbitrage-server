package me.qiujun.arbitrage.adapter.bybit.bean.event;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitAccountEvent {

    @JSONField(name = "e")
    private String eventType;

    @JSONField(name = "E")
    private Long eventTime;

    @JSONField(name = "T")
    private Boolean trade;

    @JSONField(name = "W")
    private Boolean withdrawal;

    @JSONField(name = "D")
    private Boolean deposit;

    @JSONField(name = "B")
    private List<Item> items;

    @Data
    public static class Item {

        @JSONField(name = "a")
        private String coin;

        @JSONField(name = "f")
        private BigDecimal free;

        @JSONField(name = "l")
        private BigDecimal locked;

    }

}
