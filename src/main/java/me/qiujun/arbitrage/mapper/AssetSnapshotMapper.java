package me.qiujun.arbitrage.mapper;

import me.qiujun.arbitrage.bean.base.AssetSnapshot;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetSnapshotMapper {

    int batchInsert(List<AssetSnapshot> records);

    int updateByPrimaryKey(AssetSnapshot record);

    List<AssetSnapshot> selectLast24HByExchangeAndMarket(
            @Param("exchange") ExchangeEnum exchange,
            @Param("exchangeMarket") ExchangeMarketEnum exchangeMarket
    );

}