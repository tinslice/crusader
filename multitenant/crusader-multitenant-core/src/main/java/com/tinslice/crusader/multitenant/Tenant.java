package com.tinslice.crusader.multitenant;

import java.io.Serializable;

/**
 * Identifies a tenant in a multi-tenant architecture.
 */
public interface Tenant extends Serializable {
    Object getIdentity();
}
