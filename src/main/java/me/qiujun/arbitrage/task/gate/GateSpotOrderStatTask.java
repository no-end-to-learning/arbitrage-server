package me.qiujun.arbitrage.task.gate;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.constant.AssetConstant;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.MarketPriceService;
import me.qiujun.arbitrage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.gate.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class GateSpotOrderStatTask {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketPriceService marketPriceService;

    @Scheduled(fixedRate = 3 * 1000, initialDelay = 10 * 1000)
    public void execute() throws InterruptedException {
        List<Order> orders = orderService.listNeedToStat(ExchangeEnum.GATE, ExchangeMarketEnum.SPOT);
        if (orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {
            execute(order);
        }
    }

    private void execute(Order order) {
        // Gate 手续费返现 40%
        order.setFeeAmount(order.getFeeAmount().multiply(new BigDecimal("0.6")));

        BigDecimal quotePrice = marketPriceService.getValuationPrice(order.getQuoteCurrency());
        if (order.getFeeCurrency().equals(AssetConstant.GATE_POINT)) {
            order.setStatCost(order.getFeeAmount().multiply(AssetConstant.GATE_POINT_PRICE));
        } else if (order.getFeeCurrency().equals(order.getBaseCurrency())) {
            order.setStatCost(order.getFeeAmount().multiply(order.getPrice()).multiply(quotePrice));
        } else {
            order.setStatCost(order.getFeeAmount().multiply(quotePrice));
        }

        order.setStatIncome(BigDecimal.ZERO);

        orderService.updateWithPessimisticLock(order);
    }

}
