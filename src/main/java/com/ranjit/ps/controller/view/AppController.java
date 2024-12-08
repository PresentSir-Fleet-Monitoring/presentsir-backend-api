package com.ranjit.ps.controller.view;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/")
public class AppController {


    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("home");
        return "welcome";
    }

    @ResponseBody
    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

}
