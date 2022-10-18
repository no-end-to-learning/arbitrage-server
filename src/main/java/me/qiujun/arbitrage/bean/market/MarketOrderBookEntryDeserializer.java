package me.qiujun.arbitrage.bean.market;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class MarketOrderBookEntryDeserializer implements ObjectDeserializer {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object o) {
        List<BigDecimal> bigDecimals = parser.parseArray(BigDecimal.class);
        return (T) new MarketOrderBookEntry(bigDecimals.get(0), bigDecimals.get(1));
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

}
