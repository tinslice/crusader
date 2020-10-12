package com.tinslice.crusader.multitenant.strategies;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Interface for identifying tenants from a {@link HttpServletRequest} .
 */
public interface TenantIdentificationStrategy {
    Object identifyTenant(HttpServletRequest request);
    void initialize(Map<String, Object> config);
}
