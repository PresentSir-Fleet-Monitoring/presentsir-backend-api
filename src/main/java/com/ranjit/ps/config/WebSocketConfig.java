package com.ranjit.ps.config;

import com.ranjit.ps.security.JwtTokenProvider;
import com.ranjit.ps.service.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    // Toggle to disable JWT validation for testing
    private final boolean jwtValidationEnabled = false; // Set to `true` to re-enable validation

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Enable broadcasting on /topic
        config.setApplicationDestinationPrefixes("/app"); // Prefix for client-sent messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")  // WebSocket endpoint
                .setAllowedOriginPatterns("*", "http://localhost:3000", "http://localhost:8080"); // Enable fallback for non-WebSocket clients;

        logger.debug("Registered WebSocket endpoint at /ws");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null) {
                    System.out.println("Received a message with headers: " + accessor.getNativeHeader("email"));
                }

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    System.out.println("WebSocket CONNECT request received");

                    if (!jwtValidationEnabled) {
                        // Skip JWT validation
                        logger.warn("JWT validation is disabled. Proceeding without authentication.");
                        return message;
                    }

                    try {
                        // Extract Authorization header
                        String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                            logger.warn("Missing or invalid Authorization header");
                            throw new IllegalArgumentException("Missing or invalid Authorization header");
                        }

                        // Extract and validate the JWT token
                        String token = authorizationHeader.substring(7);
                        String username = jwtTokenProvider.getUsernameFromToken(token);

                        // Log token extraction
                        logger.debug("JWT token extracted. Username: {}", username);

                        // Load user details
                        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

                        // Create authentication token and set in SecurityContext
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Attach authenticated user to WebSocket session
                        accessor.setUser(authentication);

                        logger.debug("User authentication successful for username: {}", username);

                    } catch (Exception e) {
                        logger.error("WebSocket connection unauthorized: {}", e.getMessage());
                        throw new IllegalArgumentException("WebSocket connection unauthorized: " + e.getMessage());
                    }
                }
                return message;
            }
        });
    }
}

