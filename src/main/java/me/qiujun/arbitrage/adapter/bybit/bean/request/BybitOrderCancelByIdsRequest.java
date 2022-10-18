package me.qiujun.arbitrage.adapter.bybit.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BybitOrderCancelByIdsRequest {

    private String orderIds;

    public BybitOrderCancelByIdsRequest(List<String> orderIds) {
        this.orderIds = String.join(",", orderIds);
    }

}