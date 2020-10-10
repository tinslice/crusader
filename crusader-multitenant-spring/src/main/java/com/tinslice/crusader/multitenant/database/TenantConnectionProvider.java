package com.tinslice.crusader.multitenant.database;

import com.tinslice.crusader.multitenant.MultiTenantTenantConfig;
import com.tinslice.crusader.multitenant.Tenant;
import com.tinslice.crusader.multitenant.context.TenantContextHolder;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    private final MultiTenantTenantConfig multiTenantTenantConfig;

    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public TenantConnectionProvider(MultiTenantTenantConfig multiTenantTenantConfig) {
        this.multiTenantTenantConfig = multiTenantTenantConfig;
        initDataSources();
    }

    @Override
    protected DataSource selectAnyDataSource() {
        Tenant currentTenant = TenantContextHolder.getContext().getTenant();
        String tenantId = (String) currentTenant.getIdentity();
        DataSource dataSource = this.selectDataSource(tenantId);

        if (dataSource != null) {
            return dataSource;
        }

        String[] tenants = dataSources.keySet().toArray(String[]::new);
        if (tenants.length > 0) {
            return dataSources.get(tenants[0]);
        }

        return null;
    }

    @Override
    public DataSource selectDataSource(String tenantId) {
        return dataSources.containsKey(tenantId) ? dataSources.get(tenantId) : dataSources.get(multiTenantTenantConfig.getDefaultTenant());
    }

    private void initDataSources() {
        if (multiTenantTenantConfig == null || multiTenantTenantConfig.getDataSources() == null) {
            throw new NullPointerException("datasource configuration is not defined");
        }

        multiTenantTenantConfig.getDataSources().forEach((tenantId, dataSource) -> {
            DataSourceBuilder factory = DataSourceBuilder.create()
                    .type(HikariDataSource.class)
                    .driverClassName(dataSource.getDriverClassName())
                    .url(dataSource.getUrl())
                    .username(dataSource.getUsername())
                    .password(dataSource.getPassword());
            this.dataSources.put(tenantId, factory.build());
        });
    }
}
