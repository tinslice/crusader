package com.tinslice.crusader.demo;

import com.tinslice.crusader.multitenant.MultiTenantTenantConfig;
import com.tinslice.crusader.multitenant.interceptors.SpringWebTenantIdentificationInterceptor;
import com.tinslice.crusader.multitenant.providers.DefaultTenantProvider;
import com.tinslice.crusader.multitenant.providers.TenantProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TodoWebMvcConfigurer implements WebMvcConfigurer {
    private final MultiTenantTenantConfig multiTenantTenantConfig;

    @Bean
    public TenantProvider getTenantProvider() {
        return new DefaultTenantProvider();
    }

    @Bean
    public SpringWebTenantIdentificationInterceptor getTenantIdentificationInterceptor(MultiTenantTenantConfig multiTenantTenantConfig) {
        return new SpringWebTenantIdentificationInterceptor(multiTenantTenantConfig, getTenantProvider());
    }

    public TodoWebMvcConfigurer(MultiTenantTenantConfig multiTenantTenantConfig) {
        this.multiTenantTenantConfig = multiTenantTenantConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getTenantIdentificationInterceptor(multiTenantTenantConfig));
    }
}
