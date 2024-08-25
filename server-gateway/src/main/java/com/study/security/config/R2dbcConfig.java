package com.study.security.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

import java.time.ZoneId;

@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Bean(name = "dataSource")
    @ConfigurationProperties("spring.datasource.hikari")
    @Override
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "mysql")
                .option(ConnectionFactoryOptions.HOST, "localhost")
                .option(ConnectionFactoryOptions.PORT, 3306)
                .option(ConnectionFactoryOptions.DATABASE, "security")
                .option(ConnectionFactoryOptions.USER, "root")
                .option(ConnectionFactoryOptions.PASSWORD, "root")
                .option(Option.valueOf("serverZoneId"), ZoneId.of("Asia/Seoul"))
                .build());
    }
}