package com.doubleo.didagent.domain.domain;

public enum ConnectionStatus {
    VC_OFFERED("vc_offered"),
    VC_ISSUED("vc_issued"),
    ACTIVE("active"),
    INACTIVE("inactive"),
    ERROR("error");

    private final String value;

    ConnectionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ConnectionStatus fromValue(String value) {
        for (ConnectionStatus status : ConnectionStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ConnectionStatus: " + value);
    }
}
