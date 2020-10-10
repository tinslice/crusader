package com.tinslice.crusader.multitenant.strategies;

import javax.servlet.http.HttpServletRequest;

public class HostPatternIdentificationStrategy extends AbstractHttpServletPatternIdentificationStrategy {
    public static final String NAME = "host-pattern";
    public static final String REQUESTS_HEADER = "host";

    @Override
    protected String matcherInput(HttpServletRequest request) {
        return request.getHeader(REQUESTS_HEADER);
    }
}
