package com.tinslice.crusader.multitenant.database;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.MultiTenantSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.Set;

public class TenantSpringLiquibase extends MultiTenantSpringLiquibase {
    private static final Logger logger = LoggerFactory.getLogger(TenantSpringLiquibase.class);

    private final Set<String> activeTenants;
    private final TenantConnectionProvider connectionProvider;

    private ResourceLoader resourceLoader;

    public TenantSpringLiquibase(Set<String> activeTenants, TenantConnectionProvider connectionProvider) {
        this.activeTenants = activeTenants;
        this.connectionProvider = connectionProvider;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        runOnAllDataSources();
    }

    private void runOnAllDataSources() throws LiquibaseException {

        for(String tenant : activeTenants) {
            logger.info("Initializing Liquibase for tenant '{}'", tenant);

            DataSource dataSource = connectionProvider.selectDataSource(tenant);
            if (dataSource == null) {
                logger.warn("Unable to run liquibase for tenant '{}' :: database connection is not defined", tenant);
                return;
            }

            SpringLiquibase liquibase = getSpringLiquibase(dataSource);
            liquibase.afterPropertiesSet();
            logger.info("Liquibase ran for tenant '{}'", tenant);
        }
    }

    protected final SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(this.getChangeLog());
        liquibase.setChangeLogParameters(this.getParameters());
        liquibase.setContexts(this.getContexts());
        liquibase.setLabelFilter(this.getLabelFilter());
        liquibase.setDropFirst(this.isDropFirst());
        liquibase.setClearCheckSums(this.isClearCheckSums());
        liquibase.setShouldRun(this.isShouldRun());
        liquibase.setRollbackFile(this.getRollbackFile());
        liquibase.setResourceLoader(this.resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(this.getDefaultSchema());
        liquibase.setLiquibaseSchema(this.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(this.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(this.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(this.getDatabaseChangeLogLockTable());
        return liquibase;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
