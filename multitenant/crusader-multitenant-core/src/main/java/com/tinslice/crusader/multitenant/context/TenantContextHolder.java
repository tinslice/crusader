package com.tinslice.crusader.multitenant.context;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;

/**
 * Associates a given {@link TenantContext} with the current execution thread.
 */
public final class TenantContextHolder {
    public static final String MODE_THREAD_LOCAL = "MODE_THREAD_LOCAL";
    public static final String SYSTEM_PROPERTY = "crusader.tenant.strategy";
    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
    private static TenantContextHolderStrategy strategy;

    static {
        initialize();
    }

    /**
     * Explicitly clears the context value from the current thread.
     */
    public static void clearContext() {
        strategy.clearContext();
    }

    /**
     * Obtain the current <code>TenantContext</code>.
     *
     * @return the tenant (never <code>null</code>)
     */
    public static TenantContext getContext() {
        return strategy.getContext();
    }

    /**
     * Associates a new <code>TenantContext</code> with the current thread of execution.
     *
     * @param context the new <code>TenantContext</code> (may not be <code>null</code>)
     */
    public static void setContext(TenantContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Parameter 'context' cannot be null.");
        }
        strategy.setContext(context);
    }

    /**
     * Changes the preferred strategy. Do <em>NOT</em> call this method more than once for a given JVM, as it
     * will re-initialize the strategy and adversely affect any existing threads using the old strategy.
     *
     * <code>strategyName</code> can be one of {@link TenantContextHolder#MODE_THREAD_LOCAL}
     * or class name for any implementation of {@link TenantContextHolderStrategy} .
     *
     * @param strategyName the fully qualified class name of the strategy that should be used.
     */
    public static void setStrategyName(String strategyName) {
        TenantContextHolder.strategyName = strategyName;
        initialize();
    }

    private static void initialize() {
        if ((strategyName == null) || "".equals(strategyName)) {
            // Set default
            strategyName = MODE_THREAD_LOCAL;
        }

        if (strategyName.equals(MODE_THREAD_LOCAL)) {
            strategy = new ThreadLocalTenantContextHolderStrategy();
        } else {
            // Try to load a custom strategy
            try {
                Class<?> clazz = Class.forName(strategyName);

                if (!TenantContextHolderStrategy.class.isAssignableFrom(clazz)) {
                    throw new InvalidClassException("Strategy does not implement TenantContextHolderStrategy .");
                }

                Constructor<?> customStrategy = clazz.getConstructor();
                strategy = (TenantContextHolderStrategy) customStrategy.newInstance();
            } catch (Exception e) {
                throw new TenantContextHolderInitializationException(e);
            }
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private TenantContextHolder() {
    }


}
