package me.qiujun.arbitrage.adapter.binance.bean.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceEvent<T> {

    private String stream;

    private T data;

}
