package me.qiujun.arbitrage.config.snowflake;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.config.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SnowflakeConfig {

    @Autowired
    private SystemProperties props;

    @Bean
    public Snowflake snowflake() {
        Snowflake snowflake = new Snowflake();

        if (props.getServerId() != null) {
            snowflake.setWorkerId(props.getServerId());
        }

        log.info("snowflake started with work id {}", snowflake.getWorkerId());

        return snowflake;
    }

}
