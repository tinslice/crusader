package com.tinslice.crusader.demo;

import com.tinslice.crusader.multitenant.MultiTenantTenantConfig;
import com.tinslice.crusader.multitenant.database.TenantConnectionProvider;
import com.tinslice.crusader.multitenant.database.TenantSpringLiquibase;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LiquibaseProperties.class})
public class TodoLiquibaseConfig {
    @Bean
    public TenantSpringLiquibase liquibaseMultiTenant(
            MultiTenantTenantConfig multiTenantTenantConfig,
            MultiTenantConnectionProvider connectionProvider,
            LiquibaseProperties liquibaseProperties) {

        TenantSpringLiquibase liquibase = new TenantSpringLiquibase(multiTenantTenantConfig.getActiveTenants(),
                (TenantConnectionProvider) connectionProvider);

        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());

        liquibase.setParameters(liquibaseProperties.getParameters());
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setLabels(liquibaseProperties.getLabels());
        liquibase.setClearCheckSums(liquibaseProperties.isClearChecksums());
        liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());

        return liquibase;
    }
}
