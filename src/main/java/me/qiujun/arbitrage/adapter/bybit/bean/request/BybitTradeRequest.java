package me.qiujun.arbitrage.adapter.bybit.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitTradeRequest {

    private String orderId;

}
