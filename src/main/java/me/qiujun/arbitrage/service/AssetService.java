package me.qiujun.arbitrage.service;

import me.qiujun.arbitrage.bean.base.Asset;
import me.qiujun.arbitrage.bean.base.AssetSnapshot;
import me.qiujun.arbitrage.config.snowflake.Snowflake;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.mapper.AssetMapper;
import me.qiujun.arbitrage.mapper.AssetSnapshotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AssetService {

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private AssetSnapshotMapper assetSnapshotMapper;

    @Autowired
    private Snowflake snowflake;

    private final Map<String, Map<String, Asset>> assetTable = new ConcurrentHashMap<>();

    public void set(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket, List<Asset> assets) {
        Map<String, Asset> assetMap = assets.stream()
                .collect(Collectors.toMap(Asset::getCurrency, item -> item));
        assetTable.put(getMapKey(exchange, exchangeMarket), assetMap);
    }

    public Map<String, Asset> get(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket) {
        Map<String, Asset> assetMap = assetTable.get(getMapKey(exchange, exchangeMarket));
        return assetMap != null ? assetMap : new HashMap<>();
    }

    public Asset get(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket, String currency) {
        return get(exchange, exchangeMarket).get(currency);
    }

    public void persist() {
        for (Map<String, Asset> assetMap : assetTable.values()) {
            List<Asset> assets = new ArrayList<>(assetMap.values());
            if (assets.isEmpty()) {
                continue;
            }

            // 设置主键
            for (Asset asset : assets) {
                asset.setId(snowflake.genId());
            }
            assetMapper.upsert(assets);
        }
    }

    public List<Asset> listNeedSnapshot() {
        return assetMapper.selectNeedSnapshot();
    }

    public void snapshot() {
        Long batchId = snowflake.genId();
        List<AssetSnapshot> snapshots = new ArrayList<>();
        List<Asset> assets = assetMapper.selectNeedSnapshot();
        for (Asset asset : assets) {
            AssetSnapshot snapshot = AssetSnapshot.builder()
                    .id(snowflake.genId())
                    .batchId(batchId)
                    .exchange(asset.getExchange())
                    .exchangeMarket(asset.getExchangeMarket())
                    .exchangeAccount(asset.getExchangeAccount())
                    .currency(asset.getCurrency())
                    .available(asset.getAvailable())
                    .freeze(asset.getFreeze())
                    .total(asset.getTotal())
                    .statTotal(asset.getStatTotal())
                    .build();
            snapshots.add(snapshot);
        }

        if (!snapshots.isEmpty()) {
            assetSnapshotMapper.batchInsert(snapshots);
        }
    }

    public void updateSnapshot(AssetSnapshot snapshot) {
        assetSnapshotMapper.updateByPrimaryKey(snapshot);
    }

    public List<AssetSnapshot> listLast24HSnapshots(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket) {
        return assetSnapshotMapper.selectLast24HByExchangeAndMarket(exchange, exchangeMarket);
    }

    private String getMapKey(ExchangeEnum exchange, ExchangeMarketEnum exchangeMarket) {
        return String.format("%s#%s", exchange.name(), exchangeMarket.name());
    }

}
