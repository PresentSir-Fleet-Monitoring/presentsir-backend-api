package com.ranjit.ps.sockets;

import com.ranjit.ps.model.dto.ClientInfo;
import com.ranjit.ps.service.BusQService;
import com.ranjit.ps.service.DiscordWebhookService;
import com.ranjit.ps.service.LocationService;
import com.ranjit.ps.utils.DiscordMessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ClientListener {

    private static final Logger logger = LoggerFactory.getLogger(ClientListener.class);

    private final AtomicInteger connectedClients = new AtomicInteger(0);

    private final ConcurrentHashMap<String, ClientInfo> clientSessionMap = new ConcurrentHashMap<>();

    @Autowired
    private LocationService locationService;

    @Autowired
    private DiscordWebhookService discordWebhookService;

    @Autowired
    private BusQService busQService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        try {
            String sessionId = accessor.getSessionId();

            String iam = getHeader(accessor, "iam");
            String email = getHeader(accessor, "email");
            String busIdStr = getHeader(accessor, "busId");

            if (email == null || busIdStr == null) {
                logger.warn("Missing required headers. Connection rejected.");
                return;
            }

            long busId = Long.parseLong(busIdStr);

            connectedClients.incrementAndGet();

            clientSessionMap.put(sessionId, new ClientInfo(busId, email));

            logger.info("Client connected: {} for bus {}", email, busId);

            discordWebhookService.sendDiscordMessage(
                    DiscordMessageFormatter.formatNewClientConnectedMessage(
                            String.valueOf(busId), email)
            );

            if ("sender".equals(iam)) {

                boolean added = busQService.addClientToBusQueue(busId, sessionId);

                if (!added) {
                    logger.warn("Another sender already active for bus {}", busId);
                    return;
                }

                discordWebhookService.sendDiscordMessage(
                        DiscordMessageFormatter.formatStartLocationShareMessage(
                                String.valueOf(busId), email)
                );
            }

        } catch (Exception e) {
            logger.error("Error during connect event", e);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        String sessionId = event.getSessionId();

        ClientInfo clientInfo = clientSessionMap.remove(sessionId);

        if (clientInfo == null) {
            logger.warn("Disconnect event for unknown session {}", sessionId);
            return;
        }

        connectedClients.decrementAndGet();

        locationService.removeLocationBySession(sessionId);
        busQService.removeClientFromBusQueue(clientInfo.getBusId(), sessionId);

        logger.info("Client disconnected: {}. Remaining: {}",
                clientInfo.getEmail(),
                connectedClients.get());
    }

    private String getHeader(StompHeaderAccessor accessor, String key) {
        if (accessor.getNativeHeader(key) != null &&
                !accessor.getNativeHeader(key).isEmpty()) {
            return accessor.getNativeHeader(key).get(0);
        }
        return null;
    }

    public int getConnectedClients() {
        return connectedClients.get();
    }
}
