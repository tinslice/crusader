package com.tinslice.crusader.multitenant.strategies;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementation of {@link TenantIdentificationStrategy} that matches the tenant identity from request URI.
 */
public class URIPatternIdentificationStrategy extends AbstractHttpServletPatternIdentificationStrategy {
    public static final String NAME = "uri-pattern";

    @Override
    protected String matcherInput(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
