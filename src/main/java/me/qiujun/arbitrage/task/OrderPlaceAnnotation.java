package me.qiujun.arbitrage.task;

import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OrderPlaceAnnotation {

    ExchangeEnum exchange();

    ExchangeMarketEnum exchangeMarket();

}