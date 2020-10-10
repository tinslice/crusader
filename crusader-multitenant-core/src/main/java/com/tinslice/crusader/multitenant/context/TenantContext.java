package com.tinslice.crusader.multitenant.context;

import com.tinslice.crusader.multitenant.Tenant;

import java.io.Serializable;

/**
 * Interface defining the minimum tenant information associated with the current thread of execution.
 */
public interface TenantContext extends Serializable {
    /**
     * Obtains the current tenant.
     *
     * @return the <code>Tenant</code> or <code>null</code> if no tenant information is available
     */
    Tenant getTenant();

    /**
     * Changes the current tenant or removes the tenant information.
     *
     * @param tenant the new <code>Tenant</code> or <code>null</code> if no further tenant information should be stored
     */
    void setTenant(Tenant tenant);
}
