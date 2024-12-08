package com.ranjit.ps.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AdminController {

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("adminDash", "Admin Dashboard"); // Example attribute
        return "index"; // View name
    }

    @GetMapping("/users")
    public String user(Model model) {
        model.addAttribute("manageUsers", "User Management"); // Example attribute
        return "users"; // View name
    }

    @GetMapping("/buses")
    public String buses(Model model) {
        model.addAttribute("manageBuses", "Bus Management"); // Example attribute
        return "buses"; // View name
    }
}
