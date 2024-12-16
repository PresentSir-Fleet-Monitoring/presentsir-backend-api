//package com.ranjit.ps.sockets;
//
//import com.ranjit.ps.model.dto.ClientInfo;
//import com.ranjit.ps.service.BusQService;
//import com.ranjit.ps.service.LocationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionConnectEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Component
//public class WebSocketEventListener {
//
//    @Autowired
//    private LocationService locationService;
//
//    @Autowired
//    private BusQService busQService;
//
//    private final AtomicInteger connectedClients = new AtomicInteger(0);
//    private final ConcurrentHashMap<String, ClientInfo> clientSessionMap = new ConcurrentHashMap<>();
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectEvent event) {
//        System.out.println("SessionConnectEvent triggered");
////
////        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
////        System.out.println("Headers: " + headerAccessor.toNativeHeaderMap());
////
////        try {
////            String email = headerAccessor.getNativeHeader("email").get(0);
////            long busId = Long.parseLong(headerAccessor.getNativeHeader("busId").get(0));
////            String sessionId = headerAccessor.getSessionId();
////
////            connectedClients.incrementAndGet();
////            clientSessionMap.put(sessionId, new ClientInfo(busId, email));
////            System.out.println("New client connected: Email = " + email + ", Bus ID = " + busId + ", Total: " + connectedClients.get());
////
////            busQService.addClientToBusQueue(busId, sessionId);
////        } catch (Exception e) {
////            System.err.println("Error during WebSocket connect event: " + e.getMessage());
////            e.printStackTrace();
////        }
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        System.out.println("SessionDisconnectEvent triggered");
//
//        String sessionId = event.getSessionId();
//        System.out.println("Disconnected session ID: " + sessionId);
////
////        ClientInfo clientInfo = clientSessionMap.remove(sessionId);
////        if (clientInfo != null) {
////            locationService.removeLocationBySession(sessionId);
////            busQService.removeClientFromBusQueue(clientInfo.getBusId(), sessionId);
////            connectedClients.decrementAndGet();
////            System.out.println("Client disconnected: " + clientInfo + ", Remaining clients: " + connectedClients.get());
////        } else {
////            System.out.println("Disconnected session not found: " + sessionId);
////        }
//    }
//
//    public int getConnectedClients() {
//        return connectedClients.get();
//    }
//}
