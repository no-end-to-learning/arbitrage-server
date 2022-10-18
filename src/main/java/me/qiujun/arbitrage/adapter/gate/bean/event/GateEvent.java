package me.qiujun.arbitrage.adapter.gate.bean.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateEvent<T> {

    private Long time;

    private String channel;

    private String event;

    private T result;

}
