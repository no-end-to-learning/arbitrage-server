package me.qiujun.arbitrage.adapter.bybit.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.bean.order.OrderPlaceParams;
import me.qiujun.arbitrage.enums.OrderSideEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitOrderCreateRequest {

    private String symbol;

    private String orderQty;

    private String side;

    private String orderType;

    private String orderPrice;

    public static BybitOrderCreateRequest transformFrom(OrderPlaceParams params) {
        return BybitOrderCreateRequest.builder()
                .symbol(params.getSymbol().replace("_", ""))
                .orderQty(params.getAmount().stripTrailingZeros().toPlainString())
                .side(params.getSide().toString())
                .orderType(params.getType().toString())
                .orderPrice(params.getPrice().stripTrailingZeros().toPlainString())
                .build();
    }

}