package com.tinslice.crusader.demo;

import com.tinslice.crusader.multitenant.MultiTenantConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "todo")
public class TodoConfig extends MultiTenantConfig<TodoTenantConfig> {

}
