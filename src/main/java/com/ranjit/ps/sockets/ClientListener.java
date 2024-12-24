package com.ranjit.ps.sockets;

import com.ranjit.ps.model.dto.ClientInfo;
import com.ranjit.ps.service.BusQService;
import com.ranjit.ps.service.LocationService;
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

    // This map is created because disconnect event listener is not getting headers
    private final ConcurrentHashMap<String, ClientInfo> clientSessionMap = new ConcurrentHashMap<>();

    @Autowired
    private LocationService locationService;
    @Autowired
    private BusQService busQService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        logger.info("SessionConnectEvent triggered");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Headers: " + headerAccessor.toNativeHeaderMap());

        try {
            String iam = headerAccessor.getNativeHeader("iam").get(0);
            String email = headerAccessor.getNativeHeader("email").get(0);
            long busId = Long.parseLong(headerAccessor.getNativeHeader("busId").get(0));

            String sessionId = headerAccessor.getSessionId();

            logger.info("New client connected: Email = " + email + ", Bus ID = " + busId + ", Total: " + connectedClients.get());

            // Increment to count to get no of clients connected
            connectedClients.incrementAndGet();

            clientSessionMap.put(sessionId, new ClientInfo(busId, email));

            if (Objects.equals(iam, "sender")){
                busQService.addClientToBusQueue(busId, sessionId);
            }

        } catch (Exception e) {
            logger.error("Error during WebSocket connect event: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        logger.info("SessionDisconnectEvent triggered");

        String sessionId = event.getSessionId();
        logger.info("Disconnected session ID: " + sessionId);

        // Decrement to count to get no of clients connected
        connectedClients.decrementAndGet();

        ClientInfo clientInfo = clientSessionMap.get(sessionId);

        locationService.removeLocationBySession(sessionId);
        busQService.removeClientFromBusQueue(clientInfo.getBusId(), sessionId);
        connectedClients.decrementAndGet();

        logger.info("Client removed from clientSessionMap :"+clientSessionMap.remove(sessionId));

        logger.info("Client disconnected: " + clientInfo + ", Remaining clients: " + connectedClients.get());

    }

    public int getConnectedClients() {
        return connectedClients.get();
    }

}
