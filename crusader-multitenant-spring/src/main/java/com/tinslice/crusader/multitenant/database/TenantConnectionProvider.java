package com.tinslice.crusader.multitenant.database;

import com.tinslice.crusader.multitenant.MultiTenantConfig;
import com.tinslice.crusader.multitenant.Tenant;
import com.tinslice.crusader.multitenant.TenantConfig;
import com.tinslice.crusader.multitenant.context.TenantContextHolder;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    private final MultiTenantConfig<? extends TenantConfig> multiTenantConfig;

    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public TenantConnectionProvider(MultiTenantConfig<? extends TenantConfig> multiTenantConfig) {
        this.multiTenantConfig = multiTenantConfig;
        initDataSources();
    }

    @Override
    protected DataSource selectAnyDataSource() {
        Tenant currentTenant = TenantContextHolder.getContext().getTenant();
        String tenantId = currentTenant != null ? (String) currentTenant.getIdentity() : null;
        // if in case tenantId is null we use the default tenant as we do not care what database we use
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = multiTenantConfig.getDefaultTenant();
        }
        return this.selectDataSource(tenantId);
    }

    @Override
    public DataSource selectDataSource(String tenantId) {
        if (StringUtils.isEmpty(tenantId) || !multiTenantConfig.isTenantEnabled(tenantId)) {
            return null;
        }
        return dataSources.get(multiTenantConfig.tenantConfig(tenantId).getDatabaseConnection());
    }

    private void initDataSources() {
        if (multiTenantConfig == null || CollectionUtils.isEmpty(multiTenantConfig.getDataSources())) {
            throw new NullPointerException("datasource configuration is not defined");
        }

        multiTenantConfig.getDataSources().forEach((dataSource) -> {
            DataSourceBuilder<HikariDataSource> factory = DataSourceBuilder.create()
                    .type(HikariDataSource.class)
                    .driverClassName(dataSource.getDriverClassName())
                    .url(dataSource.getUrl())
                    .username(dataSource.getUsername())
                    .password(dataSource.getPassword());
            this.dataSources.put(dataSource.getConnectionId(), factory.build());
        });
    }
}
