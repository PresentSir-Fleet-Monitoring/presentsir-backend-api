package com.ranjit.ps.model;

import com.ranjit.ps.model.dto.UserLocation;

public class BusController {

    private final String sessionId;
    private final String email;

    private volatile UserLocation lastLocation;
    private volatile long lastUpdateTime;

    public BusController(String sessionId, String email) {
        this.sessionId = sessionId;
        this.email = email;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getEmail() {
        return email;
    }

    public UserLocation getLastLocation() {
        return lastLocation;
    }

    public void updateLocation(UserLocation location) {
        this.lastLocation = location;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
