package me.qiujun.arbitrage.adapter.binance.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceExchangeInfoResponse {

    private String timezone;

    private Long serverTime;

    private List<BinanceExchangeSymbolResponse> symbols;

}
