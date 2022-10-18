package me.qiujun.arbitrage.task.gate;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.gate.GateSpotHttpClient;
import me.qiujun.arbitrage.adapter.gate.bean.request.GateSpotOrderCreateRequest;
import me.qiujun.arbitrage.adapter.gate.exception.GateException;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.bean.order.OrderPlaceParams;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.task.OrderPlaceAbstract;
import me.qiujun.arbitrage.task.OrderPlaceAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@OrderPlaceAnnotation(
        exchange = ExchangeEnum.GATE,
        exchangeMarket = ExchangeMarketEnum.SPOT
)
@ConditionalOnProperty(
        value = "app.gate.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class GateSpotOrderPlaceTask extends OrderPlaceAbstract {

    @Autowired
    private GateSpotHttpClient gateSpotHttpClient;

    @Override
    protected Boolean cancelOrder(Config config, Order order) {
        try {
            log.info("start cancel {} order {}", order.getSymbol(), order.getId());
            Boolean result = gateSpotHttpClient.cancelOrder(order.getExchangeOrderId(), order.getSymbol());
            log.info("{} order {} cancel result is {}", order.getSymbol(), order.getId(), result);
        } catch (GateException e) {
            log.warn(e.getMessage());
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    @Override
    protected Order placeOrder(Config config, OrderPlaceParams orderPlaceParams) {
        GateSpotOrderCreateRequest request = GateSpotOrderCreateRequest.transformFrom(orderPlaceParams);

        try {
            log.info("start place order, params {}", JSON.toJSONString(request));
            Order order = gateSpotHttpClient.createOrder(request).transformTo();
            log.info("order placed, exchange order id {}", order.getExchangeOrderId());
            return order;
        } catch (GateException e) {
            log.warn(e.getMessage());
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
