package com.tinslice.crusader.multitenant;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "crusader.multitenant")
public class MultiTenantConfig<T extends TenantConfig> {
    private List<DataSource> dataSources;
    private String defaultTenant;
    private Set<String> activeTenants;
    private Identification identification;
    private List<T> tenants;

    private final Map<String, T> tenantsConfigMap = new HashMap<>();

    /**
     * Check if the provided tenant is active/enabled or not.
     */
    public boolean isTenantEnabled(String tenant) {
        if (CollectionUtils.isEmpty(getActiveTenants())) {
            return false;
        }

        return getActiveTenants().contains(tenant);
    }

    /**
     * Get tenant configuration.
     */
    public T tenantConfig(String tenantId) {
        return tenantsConfigMap.get(tenantId);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        for (T tenantConfig : tenants) {
            tenantsConfigMap.put(tenantConfig.getId(), tenantConfig);
        }
    }

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

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public List<T> getTenants() {
        return tenants;
    }

    public void setTenants(List<T> tenants) {
        this.tenants = tenants;
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
        private String connectionId;
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        private HikariConfig hikariConfig;

        public String getConnectionId() {
            return connectionId;
        }

        public void setConnectionId(String connectionId) {
            this.connectionId = connectionId;
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

        public HikariConfig getHikari() {
            return hikariConfig;
        }

        public void setHikari(HikariConfig hikariConfig) {
            this.hikariConfig = hikariConfig;
        }
    }
}
