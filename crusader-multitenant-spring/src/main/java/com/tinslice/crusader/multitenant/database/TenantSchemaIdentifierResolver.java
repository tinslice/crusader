package com.tinslice.crusader.multitenant.database;

import com.tinslice.crusader.multitenant.MultiTenantTenantConfig;
import com.tinslice.crusader.multitenant.Tenant;
import com.tinslice.crusader.multitenant.context.TenantContextHolder;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class TenantSchemaIdentifierResolver implements CurrentTenantIdentifierResolver {
    private final MultiTenantTenantConfig multiTenantTenantConfig;

    public TenantSchemaIdentifierResolver(MultiTenantTenantConfig multiTenantTenantConfig) {
        this.multiTenantTenantConfig = multiTenantTenantConfig;
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        Tenant currentTenant = TenantContextHolder.getContext().getTenant();
        if (currentTenant != null) {
            return (String) currentTenant.getIdentity();
        }
        return multiTenantTenantConfig.getDefaultTenant();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
