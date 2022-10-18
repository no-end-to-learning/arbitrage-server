package me.qiujun.arbitrage.adapter.binance.bean.response;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JSONType(
        orders = {
                "openTime",
                "open",
                "high",
                "low",
                "close",
                "volume",
                "closeTime",
                "quoteAssetVolume",
                "numberOfTrades",
                "takerBuyBaseAssetVolume",
                "takerBuyQuoteAssetVolume",
                "ignore1"
        },
        serialzeFeatures = SerializerFeature.BeanToArray,
        parseFeatures = Feature.SupportArrayToBean
)
public class BinanceCandlestickResponse {

    private Long openTime;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal volume;

    private Long closeTime;

    private BigDecimal quoteAssetVolume;

    private Integer numberOfTrades;

    private BigDecimal takerBuyBaseAssetVolume;

    private BigDecimal takerBuyQuoteAssetVolume;

    private String ignore1;

}
