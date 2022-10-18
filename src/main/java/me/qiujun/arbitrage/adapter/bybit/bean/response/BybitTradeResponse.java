package me.qiujun.arbitrage.adapter.bybit.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitTradeResponse {

    private List<Item> list;

    @Data
    public static class Item {

        private String symbol;

        private String id;

        private String orderId;

        private String tradeId;

        private BigDecimal orderPrice;

        private BigDecimal orderQty;

        private BigDecimal execFee;

        private String feeTokenId;

        private Long creatTime;

        private String isBuyer;

        private String isMaker;

        private Long matchOrderId;

        private Long makerRebate;

        private Long executionTime;
    }

}
