package com.ranjit.ps.controller.api;

import com.ranjit.ps.exceptions.UserNotFoundException;
import com.ranjit.ps.model.User;
import com.ranjit.ps.model.dto.RegisterUserRequest;
import com.ranjit.ps.security.JwtTokenProvider;
import com.ranjit.ps.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request) {
        User user = request.getUser();
        long busId = request.getBusId();

        if (userService.isUserExist(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with this email already exists.");
        }

        try {
            User registeredUser = userService.registerUser(user, busId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Account created successfully",
                            "userId", registeredUser.getEmail()
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the account.");
        }
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
