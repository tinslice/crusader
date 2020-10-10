package com.tinslice.crusader.multitenant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "crusader.multitenant")
public class MultiTenantTenantConfig {
    private Map<String, DataSource> dataSources;
    private String defaultTenant;
    private Set<String> activeTenants;
    private Identification identification;

    public String getDefaultTenant() {
        if (defaultTenant == null || "".equals(defaultTenant.trim())) {
            return null;
        }
        return defaultTenant;
    }

    public void setDefaultTenant(String defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

    public Set<String> getActiveTenants() {
        return activeTenants;
    }

    public void setActiveTenants(Set<String> activeTenants) {
        this.activeTenants = activeTenants;
    }

    public Identification getIdentification() {
        return identification;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public static final class Identification {

        private Map<String, Map<String, Object>> strategies;

        public Map<String, Map<String, Object>> getStrategies() {
            return strategies;
        }

        public void setStrategies(Map<String, Map<String, Object>> strategies) {
            this.strategies = strategies;
        }
    }

    public static final class DataSource {
        public static final String DEFAULT_CONNECTION_POOL_SIZE = "10";

        private String name;
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        private String maximumPoolSize;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(String maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }
    }
}
