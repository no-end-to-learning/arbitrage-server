package me.qiujun.arbitrage.task.gate;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.gate.GateSpotHttpClient;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateSpotSymbolResponse;
import me.qiujun.arbitrage.bean.base.SymbolConfig;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import me.qiujun.arbitrage.enums.ExchangeMarketEnum;
import me.qiujun.arbitrage.service.MarketSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(
        value = "app.gate.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class GateSpotSymbolSyncTask {

    @Autowired
    private GateSpotHttpClient gateSpotHttpClient;

    @Autowired
    private MarketSymbolService marketSymbolService;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void execute() {
        List<SymbolConfig> symbolConfigs = gateSpotHttpClient.listSymbols().stream()
                .map(GateSpotSymbolResponse::transformTo)
                .collect(Collectors.toList());
        marketSymbolService.setConfigs(ExchangeEnum.GATE, ExchangeMarketEnum.SPOT, symbolConfigs);
    }

}
