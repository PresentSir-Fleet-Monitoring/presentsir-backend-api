package com.ranjit.ps.model.dto;

import com.ranjit.ps.model.User;
import jakarta.validation.Valid;

public class RegisterUserRequest {
    @Valid
    private User user;
    private long busId;

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getBusId() {
        return busId;
    }

    public void setBusId(long busId) {
        this.busId = busId;
    }
}
