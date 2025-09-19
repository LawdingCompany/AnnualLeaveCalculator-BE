package com.lawding.leavecalc.global.config;

import com.lawding.leavecalc.global.filter.InternalAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<InternalAuthFilter> internalAuthFilter() {
        FilterRegistrationBean<InternalAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new InternalAuthFilter());
        registration.addUrlPatterns("/*"); // 필터 적용할 경로
        registration.setOrder(1); // 다른 필터와의 순서 (낮을수록 먼저 실행)
        return registration;
    }
}
