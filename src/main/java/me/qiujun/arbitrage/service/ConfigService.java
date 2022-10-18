package me.qiujun.arbitrage.service;

import me.qiujun.arbitrage.bean.base.Config;
import me.qiujun.arbitrage.enums.ConfigStatusEnum;
import me.qiujun.arbitrage.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    private final Long RECENT_UPDATE_TTL = 60000L;

    public Config getById(Long id) {
        return repairStatus(configMapper.selectById(id));
    }

    public List<Config> list() {
        return configMapper.selectAll().stream()
                .map(this::repairStatus)
                .collect(Collectors.toList());
    }

    /**
     * @param config 配置
     * @return
     */
    private Config repairStatus(Config config) {
        if (config != null) {
            // 状态为 OFFLINE 且 短时间内有更新，则状态为 SUSPEND
            if (config.getStatus() == ConfigStatusEnum.OFFLINE) {
                if (System.currentTimeMillis() - config.getUpdatedAt().getTime() < RECENT_UPDATE_TTL) {
                    config.setStatus(ConfigStatusEnum.SUSPEND);
                }
            }
        }

        return config;
    }
}
