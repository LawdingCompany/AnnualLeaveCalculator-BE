package com.lawding.leavecalc.domain.global.config;

import com.lawding.leavecalc.domain.global.filter.InternalAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
public class FilterConfig {
    @Bean
    public InternalAuthFilter internalAuthFilter(@Value("${internal.secret}") String secret) {
        return new InternalAuthFilter(secret);
    }
    @Bean
    public FilterRegistrationBean<InternalAuthFilter> internalAuthFilterRegistration(InternalAuthFilter filter) {
        FilterRegistrationBean<InternalAuthFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(1);
        reg.addUrlPatterns("/*");
        return reg;
    }

}
