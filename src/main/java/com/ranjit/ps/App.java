package com.ranjit.ps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

import java.net.InetAddress;

@SpringBootApplication
public class App {

    @Value("${server.port:8080}")
    private String port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printWebSocketUrl() throws Exception {

        String ip = InetAddress.getLocalHost().getHostAddress();

        System.out.println("======================================");
        System.out.println("🚀 Application Started Successfully");
        System.out.println("🌐 WebSocket Endpoint (SockJS enabled):");
        System.out.println("👉 ws://" + ip + ":" + port + contextPath + "/ws");

        System.out.println("\n🟢 STOMP Destinations (Bus Location):");
        System.out.println("   ➤ Send bus location (driver): /app/bus-location");
        System.out.println("   ➤ Subscribe live bus location: /topic/bus-location/{busId}");
        System.out.println("   ➤ Send public discovery pin: /app/public-location");
        System.out.println("   ➤ Subscribe public pins: /topic/public-users");
        System.out.println("======================================");
    }
}
