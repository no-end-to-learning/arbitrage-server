package me.qiujun.arbitrage.bean.event;

import lombok.Getter;
import me.qiujun.arbitrage.bean.base.Config;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class CurrentConfigsChangeEvent extends ApplicationEvent {

    private final List<Config> oldConfigs;

    private final List<Config> newConfigs;

    public CurrentConfigsChangeEvent(Object source, List<Config> oldConfigs, List<Config> newConfigs) {
        super(source);
        this.oldConfigs = oldConfigs;
        this.newConfigs = newConfigs;
    }

}
