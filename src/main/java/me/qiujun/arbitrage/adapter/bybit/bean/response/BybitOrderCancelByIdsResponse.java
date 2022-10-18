package me.qiujun.arbitrage.adapter.bybit.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitOrderCancelByIdsResponse {

    private List<Item> list;

    @Data
    public static class Item {

        private String orderId;

        private String code;

    }

}
