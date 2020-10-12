package com.tinslice.crusader.multitenant.context;

/**
 * Exception thrown if an error occurred when trying to initialize the <code>TenantContextHolder</code> .
 */
public class TenantContextHolderInitializationException extends RuntimeException {
    public TenantContextHolderInitializationException() {
        super();
    }

    public TenantContextHolderInitializationException(String message) {
        super(message);
    }

    public TenantContextHolderInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TenantContextHolderInitializationException(Throwable cause) {
        super(cause);
    }

    protected TenantContextHolderInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
