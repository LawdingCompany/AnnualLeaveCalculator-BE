package com.lawding.leavecalc.domain.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${app.db.host}")
    private String host;

    @Value("${app.db.port}")
    private int port;

    @Value("${app.db.name}")
    private String dbName;

    @Value("${app.db.username}")
    private String username;

    @Value("${app.db.region}")
    private String region;

    @Bean
    public DataSource dataSource() {
        RdsUtilities rdsUtilities = RdsUtilities.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=true&requireSSL=true&verifyServerCertificate=false",
            host, port, dbName));
        config.setUsername(username);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // 매번 새로운 토큰 생성 (권장 방식)
        config.setPasswordSupplier(() ->
            rdsUtilities.generateAuthenticationToken(
                GenerateAuthenticationTokenRequest.builder()
                    .hostname(host)
                    .port(port)
                    .username(username)
                    .build()
            )
        );

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setMaxLifetime(840_000);
        config.setIdleTimeout(600_000);
        config.setConnectionTimeout(30_000);
        config.setValidationTimeout(5_000);
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }
}