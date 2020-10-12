package com.tinslice.crusader.multitenant.providers;

import com.tinslice.crusader.multitenant.Tenant;

/**
 * Interface for providing a tenant for a given identity.
 */
public interface TenantProvider {
    /**
     * Find a tenant based on the given identity.
     *
     * @param identity the identity of the tenant
     * @return a tenant for the given identity or <code>null</code>
     */
    Tenant findTenant(Object identity);
}
