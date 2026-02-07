package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.dto.UserLocation;
import com.ranjit.ps.model.dto.PublicLocation; // Import the new DTO
import com.ranjit.ps.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private LocationService locationService;

    /**
     * EXISTING: Handle high-frequency bus location updates.
     */
    @MessageMapping("/location")
    public void updateLocation(StompHeaderAccessor headerAccessor, UserLocation userLocation) {
        logger.info("Bus Location: Email = {}, Bus ID = {}", userLocation.getUserEmail(), userLocation.getBusId());
        
        String sessionId = headerAccessor.getSessionId();
        locationService.storeLocation(sessionId, userLocation);
        locationService.broadcastToBus(userLocation.getBusId());
    }

    /**
     * NEW: Handle Snapchat-style Discovery Pins (Footprints).
     * Broadcasts to all users subscribed to /topic/public-users.
     */
    @MessageMapping("/public-location")
    @SendTo("/topic/public-users")
    public PublicLocation dropDiscoveryPin(@Payload PublicLocation location) {
        // Log the discovery pin drop
        logger.info("Discovery Pin Dropped by: {} ({}) at [{}, {}]", 
                    location.getName(), location.getEmail(), location.getLat(), location.getLng());

        // Stamp the current server time for the "time-ago" logic on frontend
        location.setTimestamp(System.currentTimeMillis());

        // Return the object; @SendTo handles the broadcast automatically
        return location;
    }
}
