package com.tinslice.crusader.multitenant.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

/**
 * Implementation of {@link TenantIdentificationStrategy} that matches the tenant identity
 * based on configuration.
 */
public class HostIdentificationStrategy implements TenantIdentificationStrategy {
    private static final String EMPTY_STRING = "";

    public static final String NAME = "host";
    public static final String HOSTS_SEPARATOR = ",";
    public static final String REQUESTS_HEADER_HOST = "host";

    private static final Logger logger = LoggerFactory.getLogger(HostIdentificationStrategy.class);

    private Map<String, String> configuration;

    protected Map<String, String> getConfiguration() {
        return configuration == null ? null : new HashMap<>(configuration);
    }

    protected void setConfiguration(Map<String, String> configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration must not be null.");
        }
        this.configuration = configuration;
    }

    @Override
    public Object identifyTenant(HttpServletRequest request) {
        String hostname = request.getHeader(REQUESTS_HEADER_HOST);
        if (logger.isTraceEnabled()) {
            logger.trace("Identify tenant for hostname : " + hostname + " | origin : " + request.getHeader("origin"));
        }

        if (configuration.containsKey(hostname)) {
            return configuration.get(hostname);
        }

        return null;
    }

    /**
     * <code>PARAM_URL_CONFIG_MAP</code> format <code>host1=tenant1,host2=tenant2,...</code> .
     */
    @Override
    public void initialize(Map<String, Object> config) {
        if (config == null) {
            setConfiguration(Collections.emptyMap());
            logger.warn("Initializing tenant identification strategy with empty configuration");
            return;
        }

        Map<String,String> urlMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : config.entrySet()) {
            if (EMPTY_STRING.equals(entry.getKey().trim())) {
                continue;
            }

            String[] hosts = this.getHosts(entry.getValue());
            for (String host : hosts) {
                if (EMPTY_STRING.equals(host.trim())) {
                    continue;
                }
                urlMap.put(host.trim(), entry.getKey().trim());
            }
        }

        setConfiguration(urlMap);
    }

    private String[] getHosts(Object hosts) {
        if (hosts instanceof String[]) {
            return (String[]) hosts;
        }

        if (hosts instanceof LinkedHashMap<?,?>) {
            return ((LinkedHashMap<String, String>) hosts).values().toArray(new String[0]);
        }

        if (hosts instanceof String) {
            return ((String) hosts).split(HOSTS_SEPARATOR);
        }

        return new String[0];
    }
}
