package com.tinslice.crusader.demo;

import com.tinslice.crusader.multitenant.MultiTenantTenantConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "todo")
public class TodoConfig extends MultiTenantTenantConfig {
}
