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
public class BybitBalanceResponse {

    private List<Item> balances;

    @Data
    public static class Item {

        private String coin;

        private String coinId;

        private BigDecimal total;

        private BigDecimal free;

        private BigDecimal locked;

    }

}
