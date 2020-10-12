package com.tinslice.crusader.multitenant.providers;

import com.tinslice.crusader.multitenant.Tenant;

/**
 * Base implementation of {@link Tenant}.
 */
public class SimpleTenant implements Tenant {
    private final Object identity;

    public SimpleTenant(Object identity) {
        this.identity = identity;
    }

    @Override
    public Object getIdentity() {
        return identity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tenant)) {
            return false;
        }

        Tenant that = (Tenant) o;

        return !(identity != null ? !identity.equals(that.getIdentity()) : that.getIdentity() != null);
    }

    @Override
    public int hashCode() {
        return identity != null ? identity.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return "SimpleTenant{identity=" + identity + '}';
    }
}
