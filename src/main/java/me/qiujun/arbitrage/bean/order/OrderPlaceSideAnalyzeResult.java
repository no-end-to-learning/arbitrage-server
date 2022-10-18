package me.qiujun.arbitrage.bean.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.bean.base.Order;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlaceSideAnalyzeResult {

    private List<Order> needCancelOrders;

    private List<BigDecimal> needPlacePrices;

}
