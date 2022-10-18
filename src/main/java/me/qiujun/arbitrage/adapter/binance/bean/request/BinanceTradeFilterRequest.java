package me.qiujun.arbitrage.adapter.binance.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceTradeFilterRequest {

    private String symbol;

    private Long startTime;

    private Long endTime;

    private Long fromId;

    private Integer limit;

}
