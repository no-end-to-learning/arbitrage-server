package me.qiujun.arbitrage.mapper;

import me.qiujun.arbitrage.bean.base.Asset;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetMapper {

    int upsert(List<Asset> assets);

    List<Asset> selectNeedSnapshot();

    List<Asset> selectByExchangeAndMarket(
            @Param("exchange") ExchangeEnum exchange,
            @Param("exchangeMarket") ExchangeMarketEnum exchangeMarket
    );

    Asset selectByExchangeAndMarketAndCurrency(
            @Param("exchange") ExchangeEnum exchange,
            @Param("exchangeMarket") ExchangeMarketEnum exchangeMarket,
            @Param("currency") String currency
    );

}