package me.qiujun.arbitrage.adapter.binance.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters define trading rules on a symbol or an exchange. Filters come in two forms: symbol filters and exchange filters.
 * <p>
 * The PRICE_FILTER defines the price rules for a symbol.
 * <p>
 * The LOT_SIZE filter defines the quantity (aka "lots" in auction terms) rules for a symbol.
 * <p>
 * The MIN_NOTIONAL filter defines the minimum notional value allowed for an order on a symbol. An order's notional value is the price * quantity.
 * <p>
 * The MAX_NUM_ORDERS filter defines the maximum number of orders an account is allowed to have open on a symbol. Note that both "algo" orders and normal orders are counted for this filter.
 * <p>
 * The MAX_ALGO_ORDERS filter defines the maximum number of "algo" orders an account is allowed to have open on a symbol. "Algo" orders are STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and TAKE_PROFIT_LIMIT orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceExchangeSymbolFilterResponse {

    // PRICE_FILTER

    private BinanceExchangeSymbolFilterTypeEnum filterType;

    /**
     * Defines the minimum price/stopPrice allowed.
     */
    private String minPrice;

    /**
     * Defines the maximum price/stopPrice allowed.
     */
    private String maxPrice;

    /**
     * Defines the intervals that a price/stopPrice can be increased/decreased by.
     */
    private String tickSize;


    // LOT_SIZE

    /**
     * Defines the minimum quantity/icebergQty allowed.
     */
    private String minQty;

    /**
     * Defines the maximum quantity/icebergQty allowed.
     */
    private String maxQty;

    /**
     * Defines the intervals that a quantity/icebergQty can be increased/decreased by.
     */
    private String stepSize;

    // MIN_NOTIONAL

    /**
     * Defines the minimum notional value allowed for an order on a symbol. An order's notional value is the price * quantity.
     */
    private String minNotional;


    // MAX_NUM_ALGO_ORDERS

    /**
     * Defines the maximum number of "algo" orders an account is allowed to have open on a symbol. "Algo" orders are STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and TAKE_PROFIT_LIMIT orders.
     */
    private String maxNumAlgoOrders;

    /**
     * MAX_NUM_ORDERS filter defines the maximum number of orders an account is allowed to have open on a symbol. Note that both "algo" orders and normal orders are counted for this filter.
     * MAX_ALGO_ORDERS filter defines the maximum number of "algo" orders an account is allowed to have open on a symbol. "Algo" orders are STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and TAKE_PROFIT_LIMIT orders.
     * ICEBERG_PARTS filter defines the maximum parts an iceberg order can have. The number of ICEBERG_PARTS is defined as CEIL(qty / icebergQty).
     */
    private String limit;

}
