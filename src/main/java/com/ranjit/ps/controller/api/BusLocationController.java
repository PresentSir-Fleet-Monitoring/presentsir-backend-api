package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.dto.UserLocation;
import com.ranjit.ps.model.dto.PublicLocation;
import com.ranjit.ps.service.BusLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class BusLocationController {

    private static final Logger logger = LoggerFactory.getLogger(BusLocationController.class);

    @Autowired
    private BusLocationService busLocationService;

    // Receive bus location from driver
    @MessageMapping("/bus-location")
    public void updateBusLocation(StompHeaderAccessor headerAccessor, UserLocation userLocation) {
        String sessionId = headerAccessor.getSessionId();
        long busId = userLocation.getBusId();

        logger.info("🚌 Bus Location Received: Email = {}, Bus ID = {}", userLocation.getUserEmail(), busId);

        // Use updated BusLocationService method
        busLocationService.updateBusLocation(busId, sessionId, userLocation);
    }

    // Receive public discovery pin
    @MessageMapping("/public-location")
    @SendTo("/topic/public-users")
    public PublicLocation dropDiscoveryPin(@Payload PublicLocation location) {
        logger.info("📍 Discovery Pin Dropped by: {} ({}) at [{}, {}]",
                location.getName(), location.getEmail(), location.getLat(), location.getLng());

        location.setTimestamp(System.currentTimeMillis());
        return location;
    }
}
