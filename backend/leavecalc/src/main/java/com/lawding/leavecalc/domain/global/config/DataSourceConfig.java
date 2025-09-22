package com.lawding.leavecalc.domain.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        return new IamTokenHikariDataSource(host, port, dbName, username, region);
    }

    /**
     * getPassword() 메서드를 오버라이드하여 매번 새로운 IAM 토큰 반환
     */
    static class IamTokenHikariDataSource extends HikariDataSource {
        private final RdsUtilities rdsUtilities;
        private final String host;
        private final int port;
        private final String username;

        public IamTokenHikariDataSource(String host, int port, String dbName, String username, String region) {
            super();

            this.host = host;
            this.port = port;
            this.username = username;

            this.rdsUtilities = RdsUtilities.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

            HikariConfig config = new HikariConfig();
            this.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=true&requireSSL=true&verifyServerCertificate=false",
                host, port, dbName));
            this.setUsername(username);
            this.setDriverClassName("com.mysql.cj.jdbc.Driver");

            this.setMaximumPoolSize(5);
            this.setMinimumIdle(1);
            this.setMaxLifetime(840_000);     // 14분(토큰 15분 만료 전에 교체)
            this.setIdleTimeout(600_000);     // 10분(idle이 오래되면 닫기)
            this.setConnectionTimeout(30_000); // 풀에서 커넥션 못 얻으면 30초 후 타임아웃
            this.setValidationTimeout(5_000); //  커넥션 검증(SELECT 1) 최대 대기 5초
            this.setConnectionTestQuery("SELECT 1");
        }
        @Override
        public String getPassword() {
            //  매번 호출될 때마다 새로운 IAM 토큰 생성
            return rdsUtilities.generateAuthenticationToken(
                GenerateAuthenticationTokenRequest.builder()
                    .hostname(host)
                    .port(port)
                    .username(username)
                    .build()
            );
        }
    }
}