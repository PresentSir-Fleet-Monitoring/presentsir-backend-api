package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.dto.UserLocation;
import com.ranjit.ps.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Handle incoming location updates from the client
    @MessageMapping("/location")
    public void updateLocation(StompHeaderAccessor headerAccessor, UserLocation userLocation) {

        // Safely extract the 'email' and 'busId' from the headers
        String email = headerAccessor.getFirstNativeHeader("email");
        String busIdHeader = headerAccessor.getFirstNativeHeader("busId");

        // Ensure headers are present
        if (email == null || busIdHeader == null) {
            System.out.println("Missing required headers: email or busId");
            return; // Handle the case where headers are missing
        }

        // Convert busId to long
        long busId;
        try {
            busId = Long.parseLong(busIdHeader);
        } catch (NumberFormatException e) {
            System.out.println("Invalid busId format: " + busIdHeader);
            return;
        }

        // Log the received data
        System.out.println("Received location: Email = " + email +
                ", Bus ID = " + busId +
                ", Lat = " + userLocation.getLatitude() +
                ", Lon = " + userLocation.getLongitude());

        // Retrieve the session ID from the header
        String sessionId = headerAccessor.getSessionId(); // Get session ID from the header
        System.out.println("Session ID: " + sessionId);

        // Set the user email and busId in the UserLocation object
        userLocation.setUserEmail(email);
        userLocation.setBusId(busId);

        // Store the location in the LocationService
        locationService.storeLocation(sessionId, userLocation);

        // Optionally, broadcast the location to the bus or related clients
         locationService.broadcastToBus(busId);

    }
}
