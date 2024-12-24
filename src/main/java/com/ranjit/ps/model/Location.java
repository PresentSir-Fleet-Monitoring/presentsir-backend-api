package com.ranjit.ps.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Location {
    private double latitude;
    private double longitude;
    private long busId;

    public long getBusId() {
        return busId;
    }

    public void setBusId(long busId) {
        this.busId = busId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
