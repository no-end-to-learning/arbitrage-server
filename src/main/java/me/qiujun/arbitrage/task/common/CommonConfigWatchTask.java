package me.qiujun.arbitrage.task.common;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.bean.event.AnyConfigsChangeEvent;
import me.qiujun.arbitrage.bean.event.CurrentConfigChangeEvent;
import me.qiujun.arbitrage.bean.event.CurrentConfigsChangeEvent;
import me.qiujun.arbitrage.config.SystemProperties;
import me.qiujun.arbitrage.enums.ConfigStatusEnum;
import me.qiujun.arbitrage.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CommonConfigWatchTask {

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ApplicationEventPublisher publisher;

    private List<Config> oldConfigs = new ArrayList<>();

    @Scheduled(fixedRate = 1000)
    public void execute() {
        List<Config> newConfigs = configService.list().stream()
                .filter(item -> ConfigStatusEnum.OFFLINE != item.getStatus())
                .sorted(Comparator.comparingLong(Config::getId))
                .toList();

        emitAnyConfigsChangeEvent(oldConfigs, newConfigs);

        emitCurrentConfigsChangeEvent(oldConfigs, newConfigs);

        oldConfigs = newConfigs;
    }

    private void emitCurrentConfigsChangeEvent(List<Config> oldConfigs, List<Config> newConfigs) {
        List<Config> currentOldConfigs = filterCurrentConfigs(oldConfigs);
        List<Config> currentNewConfigs = filterCurrentConfigs(newConfigs);

        boolean isConfigChanged = false;
        Map<Long, Config> currentNewConfigMap = toConfigMap(currentNewConfigs);
        for (Config oldConfig : currentOldConfigs) {
            Config newConfig = currentNewConfigMap.get(oldConfig.getId());
            if (newConfig == null) {
                isConfigChanged = true;
                log(oldConfig, "deleted");
                publisher.publishEvent(new CurrentConfigChangeEvent(this, oldConfig, null));
            } else if (!newConfig.equals(oldConfig)) {
                isConfigChanged = true;
                log(newConfig, "updated");
                publisher.publishEvent(new CurrentConfigChangeEvent(this, oldConfig, newConfig));
            }
        }

        Map<Long, Config> currentOldConfigMap = toConfigMap(currentOldConfigs);
        for (Config newConfig : currentNewConfigs) {
            Config oldConfig = currentOldConfigMap.get(newConfig.getId());
            if (oldConfig == null) {
                isConfigChanged = true;
                log(newConfig, "created");
                publisher.publishEvent(new CurrentConfigChangeEvent(this, null, newConfig));
            }
        }

        if (isConfigChanged) {
            publisher.publishEvent(new CurrentConfigsChangeEvent(this, currentOldConfigs, currentNewConfigs));
        }
    }

    private void emitAnyConfigsChangeEvent(List<Config> oldConfigs, List<Config> newConfigs) {
        if (!oldConfigs.equals(newConfigs)) {
            publisher.publishEvent(new AnyConfigsChangeEvent(this, oldConfigs, newConfigs));
        }
    }

    private List<Config> filterCurrentConfigs(List<Config> configs) {
        return configs.stream()
                .filter(item -> item.getServerId().equals(systemProperties.getServerId()))
                .collect(Collectors.toList());
    }

    private Map<Long, Config> toConfigMap(List<Config> configs) {
        return configs.stream().collect(Collectors.toMap(Config::getId, item -> item));
    }

    private void log(Config config, String action) {
        log.info(
                "symbol {} trade in {} {} and hedge in {} {} config {}",
                config.getTradeSymbol(),
                config.getTradeExchange(),
                config.getTradeExchangeMarket(),
                config.getHedgeExchange(),
                config.getHedgeExchangeMarket(),
                action
        );
    }

}
