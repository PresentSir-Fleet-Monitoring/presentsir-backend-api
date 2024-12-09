package com.ranjit.ps.controller.view;

import com.ranjit.ps.model.User;
import com.ranjit.ps.repository.BusRepository;
import com.ranjit.ps.service.DiscordWebhookService;
import com.ranjit.ps.service.UserService;
import com.ranjit.ps.utils.DiscordMessageFormatter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BusRepository busRepository;

    // Show registration page
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        System.out.println("caoo");
        model.addAttribute("user", new User());
        model.addAttribute("buses", busRepository.findAll());
        return "register";
    }

    // Handle registration form submission
    @PostMapping("/register")
    public  ResponseEntity<String> registerUser(@ModelAttribute("user") @Valid User user, BindingResult result,
                               @RequestParam("busId") long busId) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid user data.");
        }

        if (userService.isUserExist(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this email already exists.");
        }

        try {
            userService.registerUser(user,busId);

            String messages = DiscordMessageFormatter.formatUserJoinedMessage(user);
            new DiscordWebhookService().sendDiscordMessage(messages);

            return ResponseEntity.ok("User registered successfully. Please log in.");
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during registration.");
        }
    }

    // Show login page
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        return "login";
    }

    // Redirect based on role after login
    @GetMapping("/defaultPage")
    public String redirectToHomePage(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
                ? "redirect:/index" : "redirect:/success";
    }

    // Success page for general users
    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }
}
