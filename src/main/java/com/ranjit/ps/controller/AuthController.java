package com.ranjit.ps.controller;

import com.ranjit.ps.model.DiscordMessage;
import com.ranjit.ps.model.User;
import com.ranjit.ps.repository.BusRepository;
import com.ranjit.ps.service.DiscordWebhookService;
import com.ranjit.ps.service.UserService;
import com.ranjit.ps.utils.DiscordMessageFormatter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult result,
                               @RequestParam("busId") long busId) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(user);

            String messages = DiscordMessageFormatter.formatUserJoinedMessage(user);
            new DiscordWebhookService().sendDiscordMessage(messages);

            return "redirect:/login";
        } catch (Exception e) {
            result.rejectValue("email", "error.user", "An error occurred during registration.");
            return "register";
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
