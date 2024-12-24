package com.ranjit.ps.model.dto;

public class ClientInfo {
    private long busId;
    private String email;

    public ClientInfo(long busId, String email) {
        this.busId = busId;
        this.email = email;
    }

    public long getBusId() {
        return busId;
    }

    public void setBusId(long busId) {
        this.busId = busId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
