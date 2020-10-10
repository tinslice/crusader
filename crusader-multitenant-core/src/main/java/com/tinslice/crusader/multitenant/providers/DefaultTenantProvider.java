package com.tinslice.crusader.multitenant.providers;

import com.tinslice.crusader.multitenant.Tenant;

/**
 * Provider for the default tenant.
 */
public class DefaultTenantProvider implements TenantProvider {

    @Override
    public Tenant findTenant(Object identity) {
        return new SimpleTenant(identity);
    }
}
