package me.qiujun.arbitrage.task.common;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommonAssetPersistTask {

    @Autowired
    private AssetService assetService;

    @Scheduled(fixedRate = 1000)
    public void persist() {
        assetService.persist();
    }

}
