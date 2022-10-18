package me.qiujun.arbitrage.bean.market;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JSONType(
        serializer = MarketOrderBookEntrySerializer.class,
        deserializer = MarketOrderBookEntryDeserializer.class
)
public class MarketOrderBookEntry {

    private BigDecimal price;

    private BigDecimal qty;

}
