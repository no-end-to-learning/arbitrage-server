package me.qiujun.arbitrage.adapter.gate.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateErrorResponse {

    private String label;

    private String message;

}
