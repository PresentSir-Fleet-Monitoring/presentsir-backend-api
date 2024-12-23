package com.ranjit.ps.service;

import com.ranjit.ps.exceptions.LocationNotFoundException;
import com.ranjit.ps.exceptions.SessionNotFoundException;
import com.ranjit.ps.model.dto.UserLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * LocationService is a Spring service class that manages a collection of BusQ objects.
 * It allows creating, retrieving, updating, and processing location clients associated with buses.
 *
 * Copyright © 2024 Ranjit
 * Author: Ranjit
 */
@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
    // Map to store client locations based on session ID
    private final Map<String, UserLocation> clientLocationMap = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final BusQService busQService;

    @Autowired
    public LocationService(SimpMessagingTemplate messagingTemplate, BusQService busQService) {
        this.messagingTemplate = messagingTemplate;
        this.busQService = busQService;
    }

    // Store location data for a specific session (or email)
    public void storeLocation(String sessionId, UserLocation userLocation) {
        clientLocationMap.put(sessionId, userLocation);
        logger.info("Stored location for session: " + sessionId + ", Location: " + userLocation);
    }

    // Retrieve location data for a specific session (or email)
    public UserLocation getLocationBySession(String sessionId) {
        return clientLocationMap.get(sessionId); // Returns null if session not found
    }

    // Remove location data for a specific session (or email)
    public void removeLocationBySession(String sessionId) {
        clientLocationMap.remove(sessionId); // Returns null if session not found
        logger.info("Client Removed from clientLocationMap ID : "+sessionId);
    }

    public UserLocation getLocationForClient(String sessionId) {
        UserLocation userLocation = getLocationBySession(sessionId);
        if (userLocation != null) {
            logger.info("Retrieved location for session: " + sessionId + " Location: " + userLocation);
            return userLocation;
        } else {
            logger.error("No location found for session: " + sessionId);
            return null; // or handle as per your requirements
        }
    }

    // Method to send a message to a specific client using their session ID
    public UserLocation broadcastToBus(long busId) {
        String sessionId = null;
        UserLocation userLocation = null;

        try {
            // Retrieve the session ID for the given busId
            //TODO ADD extra filter very important Ranjit this is core part of application
            sessionId = busQService.getLocationProviderClient(busId);

            // Validate if a session ID was found for the bus
            if (sessionId == null || sessionId.isEmpty()) {
                String errorMsg = "No valid session found for busId: " + busId;
                System.err.println(errorMsg);
                throw new SessionNotFoundException(errorMsg);
            }

            // Retrieve the user location using the session ID
            userLocation = getLocationBySession(sessionId);

            // Validate if location data was found for the session
            if (userLocation == null) {
                String errorMsg = "No location data found for session: " + sessionId;
                System.err.println(errorMsg);
                throw new LocationNotFoundException(errorMsg);
            }

            // Broadcast the location update to the user
            messagingTemplate.convertAndSend("/topic/location/" + busId, userLocation);


            System.out.println("Broadcasted location update for busId: " + busId + ", Location: " + userLocation);

        } catch (SessionNotFoundException | LocationNotFoundException e) {
            // Handle specific exceptions related to missing session or location
            System.err.println("Error broadcasting location for busId: " + busId + ". Error: " + e.getMessage());
            // Optionally, rethrow the exception or handle accordingly (e.g., return null)
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            String errorMsg = "Unexpected error occurred while broadcasting location for busId: " + busId;
            System.err.println(errorMsg);
            e.printStackTrace();  // Log full stack trace for debugging purposes
            // Optionally, send an alert or report the issue to a monitoring system
        }

        return userLocation;
    }
}
