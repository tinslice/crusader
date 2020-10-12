package com.tinslice.crusader.multitenant.database;

import com.tinslice.crusader.multitenant.MultiTenantConfig;
import com.tinslice.crusader.multitenant.Tenant;
import com.tinslice.crusader.multitenant.TenantConfig;
import com.tinslice.crusader.multitenant.context.TenantContextHolder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    private static final Logger logger = LoggerFactory.getLogger(TenantConnectionProvider.class);

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
            if (StringUtils.isEmpty(tenantId) && multiTenantConfig.getActiveTenants().size() > 0) {
                tenantId = multiTenantConfig.getActiveTenants().toArray(new String[0])[0];
            }
        }
        return this.selectDataSource(tenantId);
    }

    @Override
    public DataSource selectDataSource(String tenantId) {
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = multiTenantConfig.getDefaultTenant();
        }

        if (StringUtils.isEmpty(tenantId)
                || !multiTenantConfig.isTenantEnabled(tenantId)) {
            logger.error("unable to identify database connection for tenant '{}' :: tenant not enabled", tenantId);
            return null;
        }

        TenantConfig tenantConfig = multiTenantConfig.tenantConfig(tenantId);
        if (tenantConfig == null) {
            logger.error("unable to identify database connection for tenant '{}' :: tenant config is not defined", tenantId);
            return null;
        }

        return dataSources.get(tenantConfig.getDatabaseConnection());
    }

    private void initDataSources() {
        logger.info("Initialize database connections.");
        if (multiTenantConfig == null || CollectionUtils.isEmpty(multiTenantConfig.getDataSources())) {
            throw new NullPointerException("datasource configuration is not defined");
        }

        multiTenantConfig.getDataSources().forEach((dataSource) ->
                this.dataSources.put(dataSource.getConnectionId(), createDatasourceInstance(dataSource)));
    }

    /**
     * Create datasource instance base on provided configuration.
     * This method is called for all defined datasource configurations.
     */
    protected DataSource createDatasourceInstance(MultiTenantConfig.DataSource dataSourceConfig) {
        HikariConfig hikariConfig = dataSourceConfig.getHikari();
        hikariConfig.setJdbcUrl(dataSourceConfig.getUrl());
        hikariConfig.setUsername(dataSourceConfig.getUsername());
        hikariConfig.setPassword(dataSourceConfig.getPassword());
        hikariConfig.setDriverClassName(dataSourceConfig.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }
}
