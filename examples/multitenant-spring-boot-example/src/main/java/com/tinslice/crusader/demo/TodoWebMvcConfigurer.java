package com.tinslice.crusader.demo;

import com.tinslice.crusader.multitenant.interceptors.SpringWebTenantIdentificationInterceptor;
import com.tinslice.crusader.multitenant.providers.DefaultTenantProvider;
import com.tinslice.crusader.multitenant.providers.TenantProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TodoWebMvcConfigurer implements WebMvcConfigurer {
    private final TodoConfig multiTenantConfig;

    @Bean
    public TenantProvider getTenantProvider() {
        return new DefaultTenantProvider();
    }

    @Bean
    public SpringWebTenantIdentificationInterceptor getTenantIdentificationInterceptor(TodoConfig multiTenantConfig) {
        return new SpringWebTenantIdentificationInterceptor(multiTenantConfig, getTenantProvider());
    }

    public TodoWebMvcConfigurer(TodoConfig multiTenantConfig) {
        this.multiTenantConfig = multiTenantConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getTenantIdentificationInterceptor(multiTenantConfig));
    }
}
