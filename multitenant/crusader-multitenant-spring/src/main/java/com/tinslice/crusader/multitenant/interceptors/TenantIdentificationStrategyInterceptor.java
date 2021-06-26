package com.tinslice.crusader.multitenant.interceptors;

import com.tinslice.crusader.multitenant.strategies.TenantIdentificationStrategy;
import com.tinslice.crusader.multitenant.strategies.TenantIdentificationStrategyFactory;

import java.util.Map;

public interface TenantIdentificationStrategyInterceptor {
    void registerIdentificationStrategy(TenantIdentificationStrategy tenantIdentificationStrategy);
    default TenantIdentificationStrategy getTenantIdentificationStrategyInstance(String strategy, Map<String, Object> config) throws InstantiationException {
        return TenantIdentificationStrategyFactory.instance(strategy, config);
    }
}
