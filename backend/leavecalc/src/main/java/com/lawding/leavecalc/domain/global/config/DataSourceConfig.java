package com.lawding.leavecalc.domain.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // ✅ AWS JDBC Driver
        config.setDriverClassName("software.aws.rds.jdbc.mysql.Driver");

        // ✅ JDBC URL (IAM 플러그인 사용)
        config.setJdbcUrl(String.format(
            "jdbc:mysql:aws://%s:%d/%s?wrapperPlugins=iam&sslMode=REQUIRED",
            host, port, dbName
        ));

        config.setUsername(username);

        // ✅ 풀 설정 (토큰 15분 만료 전에 커넥션 교체)
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setMaxLifetime(600_000);     // 10분
        config.setIdleTimeout(600_000);     // 10분
        config.setConnectionTimeout(30_000);
        config.setValidationTimeout(5_000);
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }
}
