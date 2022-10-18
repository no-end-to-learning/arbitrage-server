package me.qiujun.arbitrage.adapter.binance.bean.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceExchangeSymbolResponse implements Serializable {

    private String symbol; // 交易对

    private String pair; // 标的交易对

    private String contractType; // 合约类型

    private Long deliveryDate;// 交割日期

    private Long onboardDate; // 上线日期

    private String status; // 交易对状态

    private String baseAsset; // 标的资产

    private String quoteAsset; // 报价资产

    private String marginAsset; // 保证金资产

    private Integer pricePrecision; // 价格小数点位数(仅作为系统精度使用，注意同tickSize 区分）

    private Integer quantityPrecision; // 数量小数点位数(仅作为系统精度使用，注意同stepSize 区分）

    private Integer baseAssetPrecision;  // 标的资产精度

    private Integer quotePrecision; // 报价资产精度

    private BigDecimal marketTakeBound;

    private String triggerProtect; // 开启"priceProtect"的条件订单的触发阈值

    private List<BinanceExchangeSymbolFilterResponse> filters;

}
