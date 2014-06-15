package com.ruenzuo.through.models;

import com.ruenzuo.through.models.enums.ServiceType;

/**
 * Created by renzocrisostomo on 15/06/14.
 */
public class Connection {

    private ServiceType type;
    private boolean connected;

    private Connection(ConnectionBuilder builder) {
        this.type = builder.type;
        this.connected = builder.connected;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public static class ConnectionBuilder {

        private final ServiceType type;
        private final boolean connected;

        public ConnectionBuilder(ServiceType type, boolean connected) {
            this.type = type;
            this.connected = connected;
        }

        public Connection build() {
            return new Connection(this);
        }

    }

}
