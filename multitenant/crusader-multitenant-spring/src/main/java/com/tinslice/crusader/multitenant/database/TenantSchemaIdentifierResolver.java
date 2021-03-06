package com.tinslice.crusader.multitenant.database;

import com.tinslice.crusader.multitenant.MultiTenantConfig;
import com.tinslice.crusader.multitenant.Tenant;
import com.tinslice.crusader.multitenant.config.TenantConfig;
import com.tinslice.crusader.multitenant.context.TenantContextHolder;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class TenantSchemaIdentifierResolver implements CurrentTenantIdentifierResolver {
    private final MultiTenantConfig<? extends TenantConfig> multiTenantConfig;

    public TenantSchemaIdentifierResolver(MultiTenantConfig<? extends TenantConfig> multiTenantConfig) {
        this.multiTenantConfig = multiTenantConfig;
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        Tenant currentTenant = TenantContextHolder.getContext().getTenant();
        if (currentTenant != null) {
            return (String) currentTenant.getIdentity();
        }
        return multiTenantConfig.getDefaultTenant();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
