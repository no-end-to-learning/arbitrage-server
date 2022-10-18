package me.qiujun.arbitrage.bean.event;

import lombok.Getter;
import me.qiujun.arbitrage.bean.base.Config;
import org.springframework.context.ApplicationEvent;

@Getter
public class CurrentConfigChangeEvent extends ApplicationEvent {

    private final Config oldConfig;

    private final Config newConfig;

    public CurrentConfigChangeEvent(Object source, Config oldConfig, Config newConfig) {
        super(source);
        this.oldConfig = oldConfig;
        this.newConfig = newConfig;
    }

}
