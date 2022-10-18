package me.qiujun.arbitrage.adapter.bybit.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitOrderCancelResponse {

    private String success;

}
