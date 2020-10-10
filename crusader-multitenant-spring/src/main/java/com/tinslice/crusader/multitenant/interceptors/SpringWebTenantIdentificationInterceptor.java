package com.tinslice.crusader.multitenant.interceptors;

import com.tinslice.crusader.multitenant.MultiTenantConfig;
import com.tinslice.crusader.multitenant.Tenant;
import com.tinslice.crusader.multitenant.context.TenantContext;
import com.tinslice.crusader.multitenant.context.TenantContextHolder;
import com.tinslice.crusader.multitenant.providers.TenantProvider;
import com.tinslice.crusader.multitenant.strategies.TenantIdentificationStrategy;
import com.tinslice.crusader.multitenant.strategies.TenantIdentificationStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpringWebTenantIdentificationInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SpringWebTenantIdentificationInterceptor.class);

    private final MultiTenantConfig multiTenantConfig;
    private final TenantProvider tenantProvider;

    private String defaultTenant;

    private final List<TenantIdentificationStrategy> identificationStrategies = new ArrayList<>();

    public SpringWebTenantIdentificationInterceptor(MultiTenantConfig multiTenantConfig, TenantProvider tenantProvider) {
        this.multiTenantConfig = multiTenantConfig;
        this.tenantProvider = tenantProvider;
        this.init();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.identifyTenant(request);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        TenantContextHolder.clearContext();
        super.postHandle(request, response, handler, modelAndView);
    }

    private void identifyTenant(HttpServletRequest request) {
        TenantContext context = TenantContextHolder.getContext();
        Tenant tenant = null;
        for (TenantIdentificationStrategy strategy : identificationStrategies) {
            Object tenantId = strategy.identifyTenant(request);
            if (tenantId == null) {
                continue;
            }

            if (logger.isTraceEnabled()) {
                logger.trace(String.format("identified tenant '%s'", tenantId));
            }

            tenant = this.tenantProvider.findTenant(tenantId);
            break;
        }

        if (tenant == null && this.defaultTenant != null) {
            if (logger.isTraceEnabled()) {
                logger.trace(String.format("using default tenant '%s'", this.defaultTenant));
            }

            tenant = this.tenantProvider.findTenant(this.defaultTenant);
        }

        checkActiveTenant(request, tenant);
        context.setTenant(tenant);
    }

    private void checkActiveTenant(HttpServletRequest request, Tenant tenant) {
        if (tenant == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Could not identify tenant for '%s'", request.getRequestURL()));
            }
            throw new HttpServerErrorException(HttpStatus.FORBIDDEN);
        }

        String tenantIdentity = (String) tenant.getIdentity();
        // if active tenants is undefined allow all tenants
        if (multiTenantConfig.getActiveTenants() != null
                && !multiTenantConfig.getActiveTenants().isEmpty() && !multiTenantConfig.getActiveTenants().contains(tenantIdentity)) {
            logger.error(String.format("Tenant '%s' is not enabled", tenantIdentity));
            throw new HttpServerErrorException(HttpStatus.FORBIDDEN);
        }
    }
    private void init() {
        if (multiTenantConfig == null) {
            logger.warn("Unable to create tenant interceptors bean :: configuration undefined");
            return;
        }

        MultiTenantConfig.Identification tenantIdentification = multiTenantConfig.getIdentification();
        if (tenantIdentification == null) {
            logger.warn("Unable to create tenant interceptors bean :: tenant identification undefined");
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
                this.identificationStrategies.add(TenantIdentificationStrategyFactory.instance(strategy, config));
            } catch (InstantiationException e) {
                logger.error(String.format("Failed to instantiate strategy '%s'", strategy), e);
            }
        });
    }
}
