package me.qiujun.arbitrage.adapter.gate.bean.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.bean.order.OrderPlaceParams;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateSpotOrderCreateRequest {

    private String text;

    @JSONField(name = "currency_pair")
    private String currencyPair;

    private String type;

    private String account;

    private String side;

    private String amount;

    private String price;

    @JSONField(name = "time_in_force")
    private String timeInForce;

    private String iceberg;

    @JSONField(name = "auto_borrow")
    private Boolean autoBorrow;

    @JSONField(name = "auto_repay")
    private Boolean autoRepay;

    public static GateSpotOrderCreateRequest transformFrom(OrderPlaceParams params) {
        return GateSpotOrderCreateRequest.builder()
                .currencyPair(params.getSymbol())
                .type(params.getType().name().toLowerCase())
                .side(params.getSide().name().toLowerCase())
                .price(params.getPrice().stripTrailingZeros().toPlainString())
                .amount(params.getAmount().stripTrailingZeros().toPlainString())
                .build();
    }

}
