package me.qiujun.arbitrage.adapter.gate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("app.gate")
public class GateProperties {

    private String account;

    private String apiKey;

    private String secretKey;

    private String httpBaseUrl;

    private String webSocketBaseUrl;

}
