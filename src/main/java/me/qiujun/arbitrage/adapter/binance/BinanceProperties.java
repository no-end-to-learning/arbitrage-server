package me.qiujun.arbitrage.adapter.binance;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("app.binance")
public class BinanceProperties {

    private String account;

    private String apiKey;

    private String secret;

    private String futuresHttpBaseUrl;

    private String futuresWebSocketBaseUrl;

    private String spotHttpBaseUrl;

    private String spotWebSocketBaseUrl;

}
