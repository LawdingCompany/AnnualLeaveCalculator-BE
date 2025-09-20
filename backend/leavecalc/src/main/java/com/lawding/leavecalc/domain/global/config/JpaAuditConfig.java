package com.lawding.leavecalc.domain.global.config;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "kstDateTimeProvider")
public class JpaAuditConfig {
    @Bean
    public DateTimeProvider kstDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }
}
