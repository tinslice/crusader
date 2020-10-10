package com.tinslice.crusader.demo;

import com.tinslice.crusader.multitenant.database.TenantConnectionProvider;
import com.tinslice.crusader.multitenant.database.TenantSchemaIdentifierResolver;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({JpaProperties.class})
@EnableJpaRepositories(basePackages = {"com.tinslice.crusader.demo"}, transactionManagerRef = "txManager")
@EnableTransactionManagement
public class TodoDatabaseConfig {
    private final JpaProperties jpaProperties;

    public TodoDatabaseConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver(TodoConfig multiTenantConfig) {
        return new TenantSchemaIdentifierResolver(multiTenantConfig);
    }

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider(TodoConfig multiTenantConfig) {
        return new TenantConnectionProvider(multiTenantConfig);
    }

    @Value("${spring.jpa.packages-to-scan:#{null}}")
    private String packagesToScan;

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(MultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                       CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        Map<String, Object> hibernateProperties = new LinkedHashMap<>();
        hibernateProperties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
        hibernateProperties.putAll(jpaProperties.getProperties());
        LocalContainerEntityManagerFactoryBean result = new LocalContainerEntityManagerFactoryBean();
        if(packagesToScan != null) {
            //handle multiple packages separated by comma
            result.setPackagesToScan(packagesToScan.split("\\s*,\\s*"));
        }
        result.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        result.setJpaPropertyMap(hibernateProperties);

        return result;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        return entityManagerFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager txManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
