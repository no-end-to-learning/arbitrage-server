package me.qiujun.arbitrage.adapter.bybit.bean.event;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitEvent<T> {

    private String type;

    private String topic;

    @JSONField(name = "ts")
    private Long timestamp;

    private T data;

}
