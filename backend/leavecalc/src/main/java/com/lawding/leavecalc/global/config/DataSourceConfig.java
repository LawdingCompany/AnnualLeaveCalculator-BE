package com.lawding.leavecalc.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsUtilities;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${app.db.host}")
    private String host;
    @Value("${app.db.port}")
    private int port;

    @Bean
    public DataSource dataSource() {
        // AWS SDK RDS Utilities
        RdsUtilities utilities = RdsClient.builder()
            .region(Region.AP_NORTHEAST_2) // 리전 맞게 변경
            .build()
            .utilities();

        // IAM 인증 토큰 발급
        String authToken = utilities.generateAuthenticationToken(r -> r
            .hostname(host)
            .port(port)
            .username(dbUser));

        // HikariCP 설정
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(authToken); // 토큰을 비밀번호처럼 전달
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // 커넥션 풀 설정
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(600000); // 10분 (토큰 만료 15분보다 짧게)
        config.setValidationTimeout(5000);

        return new HikariDataSource(config);
    }
}
