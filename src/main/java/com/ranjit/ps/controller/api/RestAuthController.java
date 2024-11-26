package com.ranjit.ps.controller.api;

import com.ranjit.ps.exceptions.UserNotFoundException;
import com.ranjit.ps.model.User;
import com.ranjit.ps.security.JwtTokenProvider;
import com.ranjit.ps.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class RestAuthController {

    private AuthenticationManager authenticationManager;

    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    public RestAuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Fetch roles from authentication object
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        String jwt = tokenProvider.generateToken(username, roles);

        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("expiresIn", tokenProvider.getTokenExpiry());
        response.put("user", Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "bus", Map.of(
                        "busId", user.getBus().getBusId(),
                        "routeName", user.getBus().getRouteName()
                ),
                "gender", user.getGender(),
                "contact", user.getContact(),
                "roles", roles
        ));

        return ResponseEntity.ok(response);
    }

}
