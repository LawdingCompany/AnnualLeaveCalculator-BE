package com.lawding.leavecalc.domain.global.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "feedbackExecutor")
    public Executor feedbackExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // --- 풀 사이즈 설정 ---
        executor.setCorePoolSize(2);     // 기본 스레드 수
        executor.setMaxPoolSize(4);     // 최대 스레드 수
        executor.setQueueCapacity(200);  // 대기열 크기
        executor.setKeepAliveSeconds(30); // idle 스레드 유지 시간

        // --- 스레드 이름 prefix (로그 디버깅시 유용) ---
        executor.setThreadNamePrefix("feedback-async-");

        // --- 큐가 가득 찼을 때 정책 ---
        // CallerRunsPolicy: 호출한 스레드가 직접 실행 (무한 대기 방지)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
