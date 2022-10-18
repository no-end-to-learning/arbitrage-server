package me.qiujun.arbitrage.task;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Asset;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.bean.base.SymbolConfig;
import me.qiujun.arbitrage.bean.market.MarketDepth;
import me.qiujun.arbitrage.bean.order.OrderPlaceContext;
import me.qiujun.arbitrage.bean.order.OrderPlaceParams;
import me.qiujun.arbitrage.bean.order.OrderPlacePriceRange;
import me.qiujun.arbitrage.config.snowflake.Snowflake;
import me.qiujun.arbitrage.enums.OrderSideEnum;
import me.qiujun.arbitrage.enums.OrderTradeTypeEnum;
import me.qiujun.arbitrage.enums.OrderTypeEnum;
import me.qiujun.arbitrage.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class OrderPlaceAbstract {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Autowired
    private MarketSymbolService marketSymbolService;

    @Autowired
    private MarketDepthService marketDepthService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private Snowflake snowflake;

    private final Long MAX_HISTORY_SPREAD_COUNT = 20L;

    private final ThreadLocal<OrderPlaceContext> floatSpreadContextThreadLocal = new ThreadLocal<>();

    public void execute(Config config) {
        // 构建 Context
        OrderPlaceContext context = floatSpreadContextThreadLocal.get();
        if (context == null || !Objects.equals(context.getConfigId(), config.getId())) {
            context = new OrderPlaceContext(config.getId());
            floatSpreadContextThreadLocal.set(context);
        }

        // 获取 Depth
        MarketDepth hedgeDepth = marketDepthService.getFromHedgeConfig(config);
        if (hedgeDepth == null) {
            log.debug("symbol {} hedge depth is null", config.getTradeSymbol());
            return;
        } else if (context.getLastHedgeDepth() != null && hedgeDepth.getId().equals(context.getLastHedgeDepth().getId())) {
            return;
        }

        // 获取汇率
        BigDecimal exchangeRate = marketPriceService.getExchangeRate(config.getTradeSymbol(), config.getHedgeSymbol());
        if (exchangeRate == null) {
            log.debug("symbol {} exchange rate is null", config.getTradeSymbol());
            return;
        }

        // 获取交易对配置
        SymbolConfig symbolConfig = marketSymbolService.getConfigFromTradeConfig(config);
        if (symbolConfig == null) {
            log.debug("symbol {} trade config is null", config.getTradeSymbol());
            return;
        }

        // 获取进行中的订单列表
        List<Order> matchingOrders = orderService.listMatching(config.getId());

        // 计算浮动点差
        BigDecimal floatSpread = calFloatSpread(context, config, hedgeDepth);

        // 获取价格区间
        Map<OrderSideEnum, OrderPlacePriceRange> priceRangeMap = calPriceRange(config, symbolConfig, floatSpread, exchangeRate, hedgeDepth);

        // 计算撤单列表
        List<Order> needCancelOrders = calCancelList(config, matchingOrders, priceRangeMap);

        // 撤单，如果有撤单，进入下次循环
        if (!needCancelOrders.isEmpty()) {
            batchCancelOrders(config, needCancelOrders);
            return;
        }

        // 计算下单列表
        List<OrderPlaceParams> placeParamsList = calPlaceList(config, symbolConfig, matchingOrders, priceRangeMap);

        // 下单
        if (!placeParamsList.isEmpty()) {
            batchPlaceOrders(config, exchangeRate, placeParamsList);
        }
    }

    public void cancelAllOrders(Config config) {
        // 获取进行中的订单列表
        List<Order> matchingOrders = orderService.listMatching(config.getId());

        // 取消所有订单
        if (!matchingOrders.isEmpty()) {
            batchCancelOrders(config, matchingOrders);
        }
    }

    private BigDecimal calFloatSpread(OrderPlaceContext context, Config config, MarketDepth hedgeDepth) {
        if (config.getFloatSpreadScale().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 第一次设置值
        MarketDepth lastHedgeDepth = context.getLastHedgeDepth();
        context.setLastHedgeDepth(hedgeDepth);
        if (lastHedgeDepth == null) {
            return config.getMaxBaseSpread();
        }

        // 买一价变动比例
        BigDecimal lastDepthAskPrice = lastHedgeDepth.getAsks().get(0).getPrice();
        BigDecimal currentDepthAskPrice = hedgeDepth.getAsks().get(0).getPrice();
        BigDecimal askPriceDiff = currentDepthAskPrice.subtract(lastDepthAskPrice).abs();
        BigDecimal askPriceDiffRate = askPriceDiff.divide(lastDepthAskPrice, 18, RoundingMode.HALF_EVEN);

        // 卖一价变动比例
        BigDecimal lastDepthBidPrice = lastHedgeDepth.getBids().get(0).getPrice();
        BigDecimal currentDepthBidPrice = hedgeDepth.getBids().get(0).getPrice();
        BigDecimal bidPriceDiff = currentDepthBidPrice.subtract(lastDepthBidPrice).abs();
        BigDecimal bidPriceDiffRate = bidPriceDiff.divide(lastDepthBidPrice, 18, RoundingMode.HALF_EVEN);

        // 限定缓存数量
        Deque<BigDecimal> historySpreads = context.getHistorySpreads();
        historySpreads.addLast(askPriceDiffRate.max(bidPriceDiffRate));
        while (historySpreads.size() > MAX_HISTORY_SPREAD_COUNT) {
            historySpreads.removeFirst();
        }

        return historySpreads.stream()
                .max(BigDecimal::compareTo)
                .map(bigDecimal -> bigDecimal.multiply(config.getFloatSpreadScale()))
                .orElseGet(config::getMaxBaseSpread);
    }

    private Map<OrderSideEnum, OrderPlacePriceRange> calPriceRange(
            Config config,
            SymbolConfig symbolConfig,
            BigDecimal floatSpread,
            BigDecimal exchangeRate,
            MarketDepth hedgeDepth
    ) {
        BigDecimal minSpread = config.getMinBaseSpread().add(floatSpread);
        BigDecimal maxSpread = config.getMaxBaseSpread().add(floatSpread);
        BigDecimal midSpread = minSpread.add(maxSpread).divide(new BigDecimal(2), 18, RoundingMode.HALF_EVEN);

        Integer pricePrecision = symbolConfig.getPricePrecision();

        BigDecimal baseBuyPrice = hedgeDepth.getBids().get(0).getPrice()
                .divide(config.getHedgeScale(), 18, RoundingMode.HALF_EVEN)
                .multiply(exchangeRate);
        OrderPlacePriceRange buyOrderPlacePriceRange = OrderPlacePriceRange.builder()
                .bestSpread(minSpread)
                .bestPrice(baseBuyPrice.multiply(BigDecimal.ONE.subtract(minSpread)).setScale(pricePrecision, RoundingMode.DOWN))
                .placeSpread(midSpread)
                .placePrice(baseBuyPrice.multiply(BigDecimal.ONE.subtract(midSpread)).setScale(pricePrecision, RoundingMode.DOWN))
                .worstSpread(maxSpread)
                .worstPrice(baseBuyPrice.multiply(BigDecimal.ONE.subtract(maxSpread)).setScale(pricePrecision, RoundingMode.DOWN))
                .build();

        BigDecimal baseSellPrice = hedgeDepth.getAsks().get(0).getPrice()
                .divide(config.getHedgeScale(), 18, RoundingMode.HALF_EVEN)
                .multiply(exchangeRate);
        OrderPlacePriceRange sellOrderPlacePriceRange = OrderPlacePriceRange.builder()
                .bestSpread(minSpread)
                .bestPrice(baseSellPrice.multiply(BigDecimal.ONE.add(minSpread)).setScale(pricePrecision, RoundingMode.UP))
                .placeSpread(midSpread)
                .placePrice(baseSellPrice.multiply(BigDecimal.ONE.add(midSpread)).setScale(pricePrecision, RoundingMode.UP))
                .worstSpread(maxSpread)
                .worstPrice(baseSellPrice.multiply(BigDecimal.ONE.add(maxSpread)).setScale(pricePrecision, RoundingMode.UP))
                .build();

        Map<OrderSideEnum, OrderPlacePriceRange> sidePriceMap = new HashMap<>();
        sidePriceMap.put(OrderSideEnum.BUY, buyOrderPlacePriceRange);
        sidePriceMap.put(OrderSideEnum.SELL, sellOrderPlacePriceRange);
        return sidePriceMap;
    }

    private List<Order> calCancelList(Config config, List<Order> orders, Map<OrderSideEnum, OrderPlacePriceRange> priceRangeMap) {
        List<Order> needCancelOrders = new ArrayList<>();
        for (Order order : orders) {
            OrderPlacePriceRange sidePriceRange = priceRangeMap.get(order.getSide());

            // 不允许交易就撤单
            if (!config.getStatus().canTrade(order.getSide())) {
                needCancelOrders.add(order);
                continue;
            }

            // 有成交就撤单
            if (order.getStatus().hasFilled()) {
                needCancelOrders.add(order);
                continue;
            }

            // 小于最小价格就撤单
            BigDecimal minPrice = sidePriceRange.getBestPrice().min(sidePriceRange.getWorstPrice());
            if (order.getPrice().compareTo(minPrice) < 0) {
                needCancelOrders.add(order);
                continue;
            }

            // 大于最大价格就撤单
            BigDecimal maxPrice = sidePriceRange.getBestPrice().max(sidePriceRange.getWorstPrice());
            if (order.getPrice().compareTo(maxPrice) > 0) {
                needCancelOrders.add(order);
                continue;
            }
        }

        return needCancelOrders;
    }

    private List<OrderPlaceParams> calPlaceList(
            Config config,
            SymbolConfig symbolConfig,
            List<Order> orders,
            Map<OrderSideEnum, OrderPlacePriceRange> priceRangeMap
    ) {
        List<OrderPlaceParams> orderPlaceParamsList = new ArrayList<>();

        Map<OrderSideEnum, List<Order>> sideMatchingOrdersMap = orders.stream()
                .collect(Collectors.groupingBy(Order::getSide));

        for (OrderSideEnum side : OrderSideEnum.values()) {
            // 不允许交易则不下单
            if (!config.getStatus().canTrade(side)) {
                continue;
            }

            // 存在订单则不下单
            if (sideMatchingOrdersMap.containsKey(side) && !sideMatchingOrdersMap.get(side).isEmpty()) {
                continue;
            }

            // 获取花费的资产
            String spendCurrency = side == OrderSideEnum.SELL ? symbolConfig.getBaseCurrency() : symbolConfig.getQuoteCurrency();
            Asset spentAsset = assetService.get(config.getTradeExchange(), config.getTradeExchangeMarket(), spendCurrency);
            if (spentAsset == null) {
                log.debug("stop place {} {} order, spent asset not found", config.getTradeSymbol(), side);
                continue;
            }

            // 下单价格
            OrderPlacePriceRange priceRange = priceRangeMap.get(side);
            BigDecimal placePrice = priceRange.getPlacePrice();

            // 下单数量
            BigDecimal maxPlaceAmount = spentAsset.getAvailable();
            if (side == OrderSideEnum.BUY) {
                maxPlaceAmount = maxPlaceAmount.divide(placePrice, symbolConfig.getAmountPrecision(), RoundingMode.DOWN);
            }

            // 判断是否满足最小下单数量
            if (maxPlaceAmount.compareTo(config.getMinAmount()) < 0) {
                log.debug("stop place {} {} order, max place amount is {}", config.getTradeSymbol(), side, maxPlaceAmount);
                continue;
            }

            // 买单判断最大下单数量
            maxPlaceAmount = maxPlaceAmount.min(config.getMaxAmount());

            // 处理小数精度问题
            maxPlaceAmount = maxPlaceAmount.setScale(symbolConfig.getAmountPrecision(), RoundingMode.DOWN);

            OrderPlaceParams orderPlaceParams = OrderPlaceParams.builder()
                    .symbol(symbolConfig.getSymbol())
                    .type(OrderTypeEnum.LIMIT)
                    .side(side)
                    .price(placePrice)
                    .amount(maxPlaceAmount)
                    .spread(priceRange.getPlaceSpread())
                    .build();
            orderPlaceParamsList.add(orderPlaceParams);
        }

        return orderPlaceParamsList;
    }

    protected void batchCancelOrders(Config config, List<Order> orders) {
        for (Order order : orders) {
            Boolean result = this.cancelOrder(config, order);
            if (result) {
                orderService.updateCanceling(order.getId());
            }
        }
    }

    protected Boolean cancelOrder(Config config, Order order) {
        return false;
    }


    protected void batchPlaceOrders(Config config, BigDecimal exchangeRate, List<OrderPlaceParams> orderPlaceParamsList) {
        for (OrderPlaceParams orderPlaceParams : orderPlaceParamsList) {
            Order order = placeOrder(config, orderPlaceParams);
            if (order != null) {
                order.setId(snowflake.genId());
                order.setConfigId(config.getId());
                order.setTradeType(OrderTradeTypeEnum.TRADE);
                order.setSpread(orderPlaceParams.getSpread());
                order.setExchangeRate(exchangeRate);
                orderService.create(order);
            }
        }
    }

    protected Order placeOrder(Config config, OrderPlaceParams orderPlaceParams) {
        return null;
    }

}