package me.qiujun.arbitrage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.system")
public class SystemProperties {

    private Long serverId;

    private Boolean master;

}