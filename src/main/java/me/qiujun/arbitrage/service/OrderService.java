package me.qiujun.arbitrage.service;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.config.SystemProperties;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.enums.OrderStatusEnum;
import me.qiujun.arbitrage.enums.OrderTradeTypeEnum;
import me.qiujun.arbitrage.mapper.OrderMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private OrderMapper orderMapper;

    private final Map<Long, Order> orderMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void recover() {
        Long serverId = systemProperties.getServerId();
        List<Order> orders = orderMapper.recover(serverId);
        for (Order order : orders) {
            orderMap.put(order.getId(), order);
        }

        log.info("recover {} orders from database", orderMap.size());
    }

    @PreDestroy
    public void persist() {
        long persistAt = System.currentTimeMillis();
        List<Order> orders = new ArrayList<>(orderMap.values());
        if (orders.isEmpty()) {
            return;
        }

        // 数据落库
        orderMapper.persist(orders);

        // 清理无效订单
        int clearCount = 0;
        for (Order order : orders) {
            // 跳过更新时间1秒内的
            if (order.getUpdatedAt().getTime() >= persistAt) {
                continue;
            }

            // 未完结的订单
            if (order.getFinishedAt() == null) {
                continue;
            }

            // 未对冲的订单
            if (order.getTradeType() == OrderTradeTypeEnum.TRADE) {
                if (order.getBaseAmountFilled().compareTo(BigDecimal.ZERO) > 0) {
                    if (order.getHedgeOrderId() == null) {
                        continue;
                    }
                }
            }

            // 未统计的订单
            if (order.getStatIncome() == null || order.getStatCost() == null) {
                continue;
            }

            // 已对冲未统计的订单
            if (order.getHedgeOrderId() != null) {
                Order hedgeOrder = orderMap.get(order.getHedgeOrderId());
                if (hedgeOrder != null && (hedgeOrder.getStatIncome() == null || hedgeOrder.getStatCost() == null)) {
                    continue;
                }
            }

            // 从内存中删除订单
            clearCount++;
            orderMap.remove(order.getId());
        }

        log.info("there are {} orders in memory, {} have been cleared", orders.size(), clearCount);
    }

    public void sharding() {
        log.info(
                "order sharding completed, deleted before {}, copied {}, deleted after {}",
                orderMapper.deleteWhichCopiedToCanceled(),
                orderMapper.copyToCanceled(),
                orderMapper.deleteWhichCopiedToCanceled()
        );
    }

    public void create(Order order) {
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        orderMap.put(order.getId(), order);
    }

    public Boolean updateWithPessimisticLock(Order order) {
        Order mapOrder = orderMap.get(order.getId());
        if (mapOrder.getUpdatedAt().equals(order.getUpdatedAt())) {
            BeanUtils.copyProperties(order, mapOrder);
            mapOrder.setUpdatedAt(new Date());
            return true;
        }
        return false;
    }

    public void updateCanceling(Long id) {
        Order order = orderMap.get(id);
        if (order.getFinishedAt() != null) {
            return;
        }

        order.setStatus(OrderStatusEnum.CANCELING);
        order.setUpdatedAt(new Date());
    }

    public void updateHedged(List<Long> orderIds, Long hedgeOrderId) {
        for (Long orderId : orderIds) {
            Order order = orderMap.get(orderId);
            order.setHedgeOrderId(hedgeOrderId);
            order.setUpdatedAt(new Date());
        }
    }

    public Order getByExchangeOrderId(String exchangeOrderId) {
        for (Order order : orderMap.values()) {
            if (order.getExchangeOrderId().equals(exchangeOrderId)) {
                return order;
            }
        }
        return null;
    }

    public List<Order> listByHedgeId(Long hedgeId) {
        return orderMap.values().stream()
                .filter(order -> Objects.equals(order.getHedgeOrderId(), hedgeId))
                .toList();
    }

    public List<Order> listMatching(Long configId) {
        return orderMap.values().stream()
                .filter(order -> Objects.equals(order.getConfigId(), configId))
                .filter(order -> order.getTradeType() == OrderTradeTypeEnum.TRADE)
                .filter(order -> order.getStatus() != OrderStatusEnum.CANCELING)
                .filter(order -> order.getFinishedAt() == null)
                .toList();
    }

    public List<Order> listNeedToHedge(Long configId) {
        return orderMap.values().stream()
                .filter(order -> Objects.equals(order.getConfigId(), configId))
                .filter(order -> order.getTradeType() == OrderTradeTypeEnum.TRADE)
                .filter(order -> order.getBaseAmountFilled().compareTo(BigDecimal.ZERO) > 0)
                .filter(order -> order.getFinishedAt() != null)
                .filter(order -> order.getHedgeOrderId() == null)
                .toList();
    }

    public List<Order> listUnfinished(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket) {
        return orderMap.values().stream()
                .filter(order -> order.getExchange() == exchange)
                .filter(order -> order.getExchangeMarket() == exchangeMarket)
                .filter(order -> order.getFinishedAt() == null)
                .toList();
    }

    public List<Order> listNeedToStat(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket) {
        return orderMap.values().stream()
                .filter(order -> order.getExchange() == exchange)
                .filter(order -> order.getExchangeMarket() == exchangeMarket)
                .filter(order -> order.getFinishedAt() != null)
                .filter(order -> order.getStatIncome() == null || order.getStatCost() == null)
                .toList();
    }


    public Map<String, BigDecimal> selectAssetChange(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket, Date snapshotAt) {
        return orderMapper.selectAssetChange(exchange, exchangeMarket, snapshotAt).stream()
                .collect(Collectors.toMap(item -> (String) item.get("currency"), item -> (BigDecimal) item.get("amount_change")));
    }

}
