package me.qiujun.arbitrage.adapter.gate.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateWebSocketRequest {

    private Long time;

    private String channel;

    private String event;

    private Object payload;

}
