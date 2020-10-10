package com.tinslice.crusader.multitenant.strategies;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Implementation of {@link TenantIdentificationStrategy} that looks for
 * tenant identity in the request header.
 */
public class HeaderIdentificationStrategy implements TenantIdentificationStrategy {
    private static final String EMPTY_STRING = "";

    public static final String NAME = "header";
    public static final String PARAM_HEADER_NAME = "name";

    private String headerName;

    protected String getHeaderName() {
        return headerName;
    }

    protected void setHeaderName(String headerName) {
        if (headerName == null || EMPTY_STRING.equals(headerName.trim())) {
            throw new IllegalArgumentException("Header name must not be null.");
        }
        this.headerName = headerName.trim();
    }

    @Override
    public Object identifyTenant(HttpServletRequest request) {
        return request.getHeader(getHeaderName());
    }

    @Override
    public void initialize(Map<String, Object> config) {
        String header = "";

        if (config != null && config.containsKey(PARAM_HEADER_NAME)) {
            header = (String) config.get(PARAM_HEADER_NAME);
        }

        setHeaderName(header);
    }
}
