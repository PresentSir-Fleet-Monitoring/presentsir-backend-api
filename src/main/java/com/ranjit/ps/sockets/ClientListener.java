package com.ranjit.ps.sockets;

import com.ranjit.ps.service.BusControlService;
import com.ranjit.ps.service.DiscordWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class ClientListener {

    private static final Logger logger = LoggerFactory.getLogger(ClientListener.class);

    private final BusControlService busControlService;
    private final DiscordWebhookService discordWebhookService;

    public ClientListener(BusControlService busControlService,
                          DiscordWebhookService discordWebhookService) {
        this.busControlService = busControlService;
        this.discordWebhookService = discordWebhookService;
    }

    @EventListener
    public void handleConnect(SessionConnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        try {
            String iam = accessor.getFirstNativeHeader("iam");
            String email = accessor.getFirstNativeHeader("email");
            long busId = Long.parseLong(accessor.getFirstNativeHeader("busId"));
            String sessionId = accessor.getSessionId();

            logger.info("New connection: iam={}, email={}, busId={}, session={}", iam, email, busId, sessionId);

            if ("sender".equalsIgnoreCase(iam)) {

                boolean registered = busControlService.registerDriver(busId, sessionId, email);

                if (!registered) {
                    logger.warn("Bus {} already has an active driver. Connection rejected for {}", busId, email);
                    discordWebhookService.sendDiscordMessage(
                            "⚠️ Driver " + email + " tried to connect but Bus " + busId + " already has an active driver."
                    );
                    return;
                }

                // Notify Discord of new driver
                discordWebhookService.sendDiscordMessage(
                        "🟢 Driver " + email + " started sharing location for Bus " + busId
                );
            } else {
                // Optional: Notify Discord of normal client connection
                discordWebhookService.sendDiscordMessage(
                        "🔵 Client " + email + " connected to Bus " + busId
                );
            }

        } catch (Exception e) {
            logger.error("Error during WebSocket connect event: ", e);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        String sessionId = event.getSessionId();
        boolean driverRemoved = false;

        // Check all buses to remove driver if session matches
        for (var entry : busControlService.getControllerMap().entrySet()) {
            long busId = entry.getKey();
            var controller = entry.getValue();

            if (controller.getSessionId() != null && controller.getSessionId().equals(sessionId)) {
                busControlService.removeDriverIfOwner(busId, sessionId);
                driverRemoved = true;

                logger.info("Driver session disconnected: busId={}, session={}", busId, sessionId);
            }
        }

        if (!driverRemoved) {
            discordWebhookService.sendDiscordMessage(
                    "⚪ Client disconnected: session=" + sessionId
            );
            logger.info("Client session disconnected: {}", sessionId);
        }
    }
}