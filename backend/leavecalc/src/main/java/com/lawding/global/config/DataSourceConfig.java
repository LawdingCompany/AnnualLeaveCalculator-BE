package com.lawding.global.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;

import javax.sql.DataSource;

@Profile("prod")
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${app.aws.host}")
    private String host;

    @Value("${app.aws.port}")
    private int port;

    @Value("${app.aws.region}")
    private String region;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {
        return new IamTokenHikariDataSource(host, port, region, username, url);
    }

    /**
     * IAM 토큰을 매번 발급받는 커스텀 DataSource
     */
    static class IamTokenHikariDataSource extends HikariDataSource {

        private final RdsUtilities rdsUtilities;
        private final String host;
        private final int port;
        private final String username;

        public IamTokenHikariDataSource(String host, int port, String region, String username,
            String url) {
            super();

            this.host = host;
            this.port = port;
            this.username = username;

            this.rdsUtilities = RdsUtilities.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

            this.setJdbcUrl(url);
            this.setUsername(username);
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