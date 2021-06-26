package com.tinslice.crusader.multitenant.interceptors;

import com.tinslice.crusader.multitenant.MultiTenantConfig;
import com.tinslice.crusader.multitenant.Tenant;
import com.tinslice.crusader.multitenant.config.TenantConfig;
import com.tinslice.crusader.multitenant.context.TenantContextHolder;
import com.tinslice.crusader.multitenant.providers.TenantProvider;
import com.tinslice.crusader.multitenant.strategies.TenantIdentificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpringFilterTenantIdentificationInterceptor extends OncePerRequestFilter implements TenantIdentificationStrategyInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(SpringFilterTenantIdentificationInterceptor.class);

    private final MultiTenantConfig<? extends TenantConfig> multiTenantConfig;
    private final TenantProvider tenantProvider;

    private String defaultTenant;
    private final List<TenantIdentificationStrategy> identificationStrategies = new ArrayList<>();

    public SpringFilterTenantIdentificationInterceptor(MultiTenantConfig<? extends TenantConfig> multiTenantConfig, TenantProvider tenantProvider) {
        super();
        this.multiTenantConfig = multiTenantConfig;
        this.tenantProvider = tenantProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        this.identifyTenant(request);
        chain.doFilter(request, response);
    }

    @Override
    public void registerIdentificationStrategy(TenantIdentificationStrategy tenantIdentificationStrategy) {
        this.identificationStrategies.add(tenantIdentificationStrategy);
    }

    private void identifyTenant(HttpServletRequest request) {
        Tenant tenant = null;
        for (TenantIdentificationStrategy strategy : identificationStrategies) {
            Object tenantId = strategy.identifyTenant(request);
            if (tenantId == null) {
                continue;
            }

            if (logger.isTraceEnabled()) {
                logger.trace("identified tenant '{}'", tenantId);
            }

            tenant = this.tenantProvider.findTenant(tenantId);
            break;
        }

        if (tenant == null && this.defaultTenant != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("using default tenant '{}'", this.defaultTenant);
            }

            tenant = this.tenantProvider.findTenant(this.defaultTenant);
        }

        checkActiveTenant(request, tenant);
        TenantContextHolder.getContext().setTenant(tenant);
    }

    private void checkActiveTenant(HttpServletRequest request, Tenant tenant) {
        if (tenant == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not identify tenant for '{}'", request.getRequestURL());
            }
            throw new HttpServerErrorException(HttpStatus.FORBIDDEN);
        }

        String tenantIdentity = (String) tenant.getIdentity();
        // if active tenants is undefined allow all tenants
        if (multiTenantConfig.getActiveTenants() != null
                && !multiTenantConfig.getActiveTenants().isEmpty() && !multiTenantConfig.getActiveTenants().contains(tenantIdentity)) {
            logger.error("Tenant '{}' is not enabled", tenantIdentity);
            throw new HttpServerErrorException(HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        this.init();
    }

    private void init() {
        if (multiTenantConfig == null) {
            logger.warn("Unable to create tenant interceptors filter :: configuration undefined");
            return;
        }

        MultiTenantConfig.Identification tenantIdentification = multiTenantConfig.getIdentification();
        if (tenantIdentification == null) {
            logger.warn("Unable to create tenant interceptors filter :: tenant identification undefined");
            return;
        }

        this.defaultTenant = multiTenantConfig.getDefaultTenant();

        Map<String, Map<String, Object>> strategies = tenantIdentification.getStrategies();

        if (strategies == null) {
            logger.warn("Unable to create tenant interceptors bean :: strategies undefined");
            return;
        }

        strategies.forEach((strategy, config) -> {
            try {
                this.registerIdentificationStrategy(getTenantIdentificationStrategyInstance(strategy, config));
            } catch (InstantiationException e) {
                logger.error("Failed to instantiate strategy '{}'", strategy, e);
            }
        });
    }
}
