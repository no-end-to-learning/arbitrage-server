package me.qiujun.arbitrage.bean.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.enums.OrderSideEnum;
import me.qiujun.arbitrage.enums.OrderTypeEnum;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlaceParams {

    private String symbol;

    private OrderTypeEnum type;

    private OrderSideEnum side;

    private BigDecimal price;

    private BigDecimal amount;

    private BigDecimal spread;

}
