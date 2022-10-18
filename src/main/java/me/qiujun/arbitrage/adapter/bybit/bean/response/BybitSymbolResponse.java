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
public class BybitSymbolResponse {

    private List<Item> list;

    @Data
    public static class Item {

        private String name;

        private String alias;

        private String baseCoin;

        private String quoteCoin;

        private BigDecimal basePrecision;

        private BigDecimal quotePrecision;

        private BigDecimal minTradeQty;

        private BigDecimal minTradeAmt;

        private BigDecimal maxTradeQty;

        private BigDecimal maxTradeAmt;

        private BigDecimal minPricePrecision;

        private BigDecimal category;

        private BigDecimal showStatus;

        private BigDecimal innovation;

    }

}
