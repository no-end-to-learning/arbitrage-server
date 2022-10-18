package me.qiujun.arbitrage.bean.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.qiujun.arbitrage.enums.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Long id;

    private Long configId;

    private OrderTradeTypeEnum tradeType;

    private ExchangeEnum exchange;

    private ExchangeMarketEnum exchangeMarket;

    private String exchangeOrderId;

    private String symbol;

    private OrderSideEnum side;

    private OrderTypeEnum type;

    private BigDecimal price;

    private OrderStatusEnum status;

    private String baseCurrency;

    private BigDecimal baseAmount;

    private BigDecimal baseAmountFilled;

    private String quoteCurrency;

    private BigDecimal quoteAmount;

    private BigDecimal quoteAmountFilled;

    private String feeCurrency;

    private BigDecimal feeAmount;

    private BigDecimal spread;

    private BigDecimal exchangeRate;

    private Long hedgeOrderId;

    private BigDecimal statIncome;

    private BigDecimal statCost;

    private Date finishedAt;

    private Date createdAt;

    private Date updatedAt;

}