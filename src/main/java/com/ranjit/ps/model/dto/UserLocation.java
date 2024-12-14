package com.ranjit.ps.model.dto;

public class UserLocation {
    private String userEmail;
    private long busId;
    private double latitude;
    private double longitude;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

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

    @Override
    public String toString() {
        return "UserLocation{" +
                "userEmail='" + userEmail + '\'' +
                ", busId=" + busId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}