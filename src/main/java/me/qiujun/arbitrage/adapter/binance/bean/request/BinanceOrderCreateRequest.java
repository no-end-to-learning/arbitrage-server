package me.qiujun.arbitrage.adapter.binance.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.adapter.binance.enums.BinanceOrderSideEnum;
import me.qiujun.arbitrage.adapter.binance.enums.BinanceOrderTypeEnum;
import me.qiujun.arbitrage.bean.order.OrderPlaceParams;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceOrderCreateRequest {

    private String symbol;

    private BinanceOrderSideEnum side;

    private BinanceOrderTypeEnum type;

    private String timeInForce;

    private String quantity;

    private String quoteOrderQty;

    private String price;

    private String newClientOrderId;

    private String stopPrice;

    private String stopLimitPrice;

    private String icebergQty;

    private String newOrderRespType;

    public static BinanceOrderCreateRequest transformFrom(OrderPlaceParams params) {
        return BinanceOrderCreateRequest.builder()
                .symbol(params.getSymbol().replace("_", ""))
                .side(BinanceOrderSideEnum.valueOf(params.getSide()))
                .type(BinanceOrderTypeEnum.valueOf(params.getType()))
                .price(params.getPrice() == null ? null : params.getPrice().stripTrailingZeros().toPlainString())
                .quantity(params.getAmount().stripTrailingZeros().toPlainString())
                .build();
    }

}
