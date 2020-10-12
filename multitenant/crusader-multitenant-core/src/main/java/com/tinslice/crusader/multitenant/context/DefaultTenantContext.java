package com.tinslice.crusader.multitenant.context;


import com.tinslice.crusader.multitenant.Tenant;

/**
 * Base implementation of {@link TenantContext}.
 */
public class DefaultTenantContext implements TenantContext {
    private Tenant tenant;

    public DefaultTenantContext() {
        tenant = null;
    }

    public DefaultTenantContext(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public Tenant getTenant() {
        return tenant;
    }

    @Override
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TenantContext)) {
            return false;
        }

        TenantContext that = (TenantContext) o;

        return !(tenant != null ? !tenant.equals(that.getTenant()) : that.getTenant() != null);

    }

    @Override
    public int hashCode() {
        return tenant != null ? tenant.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return "TenantContext{tenant=" + tenant + '}';
    }
}
