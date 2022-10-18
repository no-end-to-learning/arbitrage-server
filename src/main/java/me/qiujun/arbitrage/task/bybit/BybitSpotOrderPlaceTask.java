package me.qiujun.arbitrage.task.bybit;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.bybit.BybitSpotHttpClient;
import me.qiujun.arbitrage.adapter.bybit.bean.request.BybitOrderCreateRequest;
import me.qiujun.arbitrage.adapter.bybit.bean.response.BybitOrderCancelByIdsResponse;
import me.qiujun.arbitrage.adapter.bybit.exception.BybitException;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.bean.order.OrderPlaceParams;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.OrderService;
import me.qiujun.arbitrage.task.OrderPlaceAbstract;
import me.qiujun.arbitrage.task.OrderPlaceAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@OrderPlaceAnnotation(
        exchange = ExchangeEnum.BYBIT,
        exchangeMarket = ExchangeMarketEnum.SPOT
)
@ConditionalOnProperty(
        value = "app.bybit.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class BybitSpotOrderPlaceTask extends OrderPlaceAbstract {

    @Autowired
    private BybitSpotHttpClient bybitSpotHttpClient;

    @Autowired
    private OrderService orderService;

    @Override
    protected void batchCancelOrders(Config config, List<Order> orders) {
        List<String> exchangeOrderIds = orders.stream().map(Order::getExchangeOrderId).collect(Collectors.toList());

        try {
            log.info("start cancel orders, params {}", exchangeOrderIds);
            List<BybitOrderCancelByIdsResponse.Item> result = bybitSpotHttpClient.cancelOrder(exchangeOrderIds);
            log.info("order canceled, result {}", result);

            for (Order order : orders) {
                orderService.updateCanceling(order.getId());
            }

        } catch (BybitException e) {
            log.warn(e.getMessage());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected Order placeOrder(Config config, OrderPlaceParams orderPlaceParams) {
        try {
            BybitOrderCreateRequest params = BybitOrderCreateRequest.transformFrom(orderPlaceParams);
            log.info("start place order, params {}", JSON.toJSONString(params));
            Order order = bybitSpotHttpClient.createOrder(params).transformTo(config.getTradeSymbol());
            log.info("order placed, exchange order id {}", order.getExchangeOrderId());
            return order;
        } catch (BybitException e) {
            log.warn(e.getMessage());
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
