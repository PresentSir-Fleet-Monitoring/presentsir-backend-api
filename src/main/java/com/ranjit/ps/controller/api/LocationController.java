package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.dto.UserLocation;
import com.ranjit.ps.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private LocationService locationService;

    /**
     * Handle incoming location updates from the client.
     *
     * @param headerAccessor the STOMP header accessor containing metadata such as session ID and custom headers.
     * @param userLocation   the UserLocation payload sent from the client.
     */
    @MessageMapping("/location")
    public void updateLocation(StompHeaderAccessor headerAccessor, UserLocation userLocation) {

        // Log received data
        logger.info("Received location: Email = {}, Bus ID = {}, Latitude = {}, Longitude = {}",
                userLocation.getUserEmail(), userLocation.getBusId(), userLocation.getLatitude(), userLocation.getLongitude());

        // Retrieve and log session ID
        String sessionId = headerAccessor.getSessionId();
        logger.info("Session ID: {}", sessionId);

        // Store the location in LocationService
        locationService.storeLocation(sessionId, userLocation);

        // Optionally broadcast the location to relevant clients
        locationService.broadcastToBus(userLocation.getBusId());
    }
}
