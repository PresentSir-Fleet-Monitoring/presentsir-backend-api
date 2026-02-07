package com.ranjit.ps.model.dto;

public class PublicLocation {
    private String email;
    private String name;
    private String pic;
    private double lat;
    private double lng;
    private long timestamp; // Added for "Last Seen" logic

    public PublicLocation() {}

    public PublicLocation(String email, String name, String pic, double lat, double lng, long timestamp) {
        this.email = email;
        this.name = name;
        this.pic = pic;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPic() { return pic; }
    public void setPic(String pic) { this.pic = pic; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
