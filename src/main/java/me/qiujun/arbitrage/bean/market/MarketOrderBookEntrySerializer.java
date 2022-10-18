package me.qiujun.arbitrage.bean.market;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class MarketOrderBookEntrySerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        MarketOrderBookEntry orderBookEntry = (MarketOrderBookEntry) object;
        List<BigDecimal> bigDecimals = Arrays.asList(orderBookEntry.getPrice(), orderBookEntry.getQty());
        serializer.write(bigDecimals);
    }

}
