package com.ranjit.ps.service;

import com.ranjit.ps.model.BusController;
import com.ranjit.ps.model.dto.UserLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BusLocationService {

    private static final Logger logger = LoggerFactory.getLogger(BusLocationService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final BusControlService busControlService;

    public BusLocationService(SimpMessagingTemplate messagingTemplate,
                              BusControlService busControlService) {
        this.messagingTemplate = messagingTemplate;
        this.busControlService = busControlService;
    }

    /**
     * Update bus location from a driver/session
     */
    public void updateBusLocation(long busId, String sessionId, UserLocation location) {

        if (!busControlService.isAuthorized(busId, sessionId)) {
            logger.warn("❌ Unauthorized location attempt for busId: {} session: {}", busId, sessionId);
            return; // Unauthorized
        }

        BusController controller = busControlService.getController(busId);
        if (controller != null) {
            controller.updateLocation(location);
            logger.info("🚌 Updated location for busId: {} from session: {}", busId, sessionId);

            // Broadcast to all subscribers
            messagingTemplate.convertAndSend(
                    "/topic/bus-location/" + busId,
                    location
            );

            logger.info("📡 Broadcasted bus-location for busId: {}", busId);
        } else {
            logger.error("❌ BusController not found for busId: {}", busId);
        }
    }

    /**
     * Get last known location of a bus
     */
    public UserLocation getCurrentBusLocation(long busId) {
        BusController controller = busControlService.getController(busId);
        return controller != null ? controller.getLastLocation() : null;
    }
}
