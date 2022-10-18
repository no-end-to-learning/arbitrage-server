package me.qiujun.arbitrage.bean.event;

import lombok.Getter;
import me.qiujun.arbitrage.bean.base.Order;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderTradeEvent extends ApplicationEvent {

    private final Order order;

    public OrderTradeEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

}
