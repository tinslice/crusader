package com.tinslice.crusader.multitenant;

public class DefaultTenantConfig implements TenantConfig{
    private String id;
    private String databaseConnection;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(String databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
}
