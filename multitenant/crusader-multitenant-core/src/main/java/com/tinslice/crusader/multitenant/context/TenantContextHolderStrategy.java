package com.tinslice.crusader.multitenant.context;

/**
 * A strategy for storing tenant information against a thread.
 */
public interface TenantContextHolderStrategy {
    /**
     * Clears the current tenant context.
     */
    void clearContext();

    /**
     * Obtains the current tenant context.
     *
     * @return a tenant context (never <code>null</code> - create a default implementation if necessary)
     */
    TenantContext getContext();

    /**
     * Sets the current context.
     *
     * @param context to the new argument (should never be <code>null</code>, although implementations must check if
     *                <code>null</code> has been passed and throw an <code>IllegalArgumentException</code> in such cases)
     */
    void setContext(TenantContext context);
}
