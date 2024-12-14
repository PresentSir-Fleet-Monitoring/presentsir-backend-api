package com.ranjit.ps.sockets;

import com.ranjit.ps.model.dto.ClientInfo;
import com.ranjit.ps.model.dto.UserLocation;
import com.ranjit.ps.service.BusQService;
import com.ranjit.ps.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocketEventListener listens for WebSocket connection and disconnection events.
 * It manages client sessions and updates the associated bus queues when clients connect or disconnect.
 *
 * Copyright © 2024 Ranjit
 * Author: Ranjit
 */
@Component
public class WebSocketEventListener {

    @Autowired
    private LocationService locationService; // Service to manage bus queues
    @Autowired
    private BusQService busQService;
    private final AtomicInteger connectedClients = new AtomicInteger(0); // Counter for connected clients
    private final ConcurrentHashMap<String, ClientInfo> clientSessionMap = new ConcurrentHashMap<>(); // Map to store client session details

    /**
     * Handles WebSocket connection events.
     * Registers a new client by extracting their email and bus ID from headers,
     * and adds them to the corresponding bus queue.
     *
     * @param event The WebSocket session connect event.
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Extract client details from WebSocket headers
        String email = headerAccessor.getNativeHeader("email").get(0);
        long busId = Long.parseLong(headerAccessor.getNativeHeader("busId").get(0));


        String sessionId = headerAccessor.getSessionId();

        // Increment connected client count and register the client
        connectedClients.incrementAndGet();
        clientSessionMap.put(sessionId, new ClientInfo(busId, email));

        System.out.println("New client connected: Email = " + email + ", Bus ID = " + busId + ", Total: " + connectedClients.get());

        // Add the client to the corresponding bus queue
        busQService.addClientToBusQueue(busId, sessionId);

    }

    /**
     * Handles WebSocket disconnection events.
     * Removes the disconnected client from the associated bus queue and updates the session map.
     *
     * @param event The WebSocket session disconnect event.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();

        ClientInfo clientInfo = clientSessionMap.remove(sessionId);
        if (clientInfo != null) {
            locationService.removeLocationBySession(sessionId);
            busQService.removeClientFromBusQueue(clientInfo.getBusId(),sessionId);
            connectedClients.decrementAndGet();
            System.out.println("Client disconnected: " + clientInfo + ", Remaining clients: " + connectedClients.get());
        } else {
            System.out.println("Disconnected session not found: " + sessionId);
        }
    }

    /**
     * Retrieves the current number of connected clients.
     *
     * @return The total number of connected clients.
     */
    public int getConnectedClients() {
        return connectedClients.get();
    }
}
