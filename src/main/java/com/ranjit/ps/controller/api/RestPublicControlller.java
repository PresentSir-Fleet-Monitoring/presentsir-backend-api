package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.Bus;
import com.ranjit.ps.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class RestPublicControlller {
    @Autowired
    private BusService busService;
    @GetMapping("/buses")
    public List<Bus> getAllBuses() {
        return busService.getAllBuses();
    }


}
