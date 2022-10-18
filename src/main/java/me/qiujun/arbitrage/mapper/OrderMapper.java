package me.qiujun.arbitrage.mapper;

import me.qiujun.arbitrage.bean.base.Order;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    List<Order> recover(Long serverId);

    int persist(List<Order> orders);

    List<Map<String, Object>> selectAssetChange(
            @Param("exchange") ExchangeEnum exchange,
            @Param("exchangeMarket") ExchangeMarketEnum exchangeMarket,
            @Param("snapshotAt") Date snapshotAt
    );

    int copyToCanceled();

    int deleteWhichCopiedToCanceled();

}