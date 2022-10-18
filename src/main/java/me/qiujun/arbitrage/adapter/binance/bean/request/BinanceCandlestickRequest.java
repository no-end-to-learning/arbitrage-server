package me.qiujun.arbitrage.adapter.binance.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceCandlestickRequest {

    private String symbol;

    private String interval;

    private Long startTime;

    private Long endTime;

    private Integer limit;

}
