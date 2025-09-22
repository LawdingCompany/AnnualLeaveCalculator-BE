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

        // IAM 토큰 생성
        String authToken = rdsUtilities.generateAuthenticationToken(
            GenerateAuthenticationTokenRequest.builder()
                .hostname(host)
                .port(port)
                .username(username)
                .build()
        );

        HikariConfig config = new HikariConfig();

        // SSL 필수 설정 추가
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=true&requireSSL=true&verifyServerCertificate=false",
            host, port, dbName));
        config.setUsername(username);
        config.setPassword(authToken); // 생성된 IAM 토큰 설정
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // IAM 토큰은 15분 만료되므로 연결 수명을 14분으로 설정
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setMaxLifetime(840_000);     // 14분 (15분 만료 전에 갱신)
        config.setIdleTimeout(600_000);     // 10분
        config.setConnectionTimeout(30_000); // 30초
        config.setValidationTimeout(5_000);  // 5초
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }
}