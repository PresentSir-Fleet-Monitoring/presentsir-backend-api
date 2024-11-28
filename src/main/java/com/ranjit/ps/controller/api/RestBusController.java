package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.Bus;
import com.ranjit.ps.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class RestBusController {

    @Autowired
    private BusService busService;

    @PostMapping
    public Bus createBus(@RequestBody Bus bus) {
        return busService.saveBus(bus);
    }

    @GetMapping
    public List<Bus> getAllBuses() {
        return busService.getAllBuses();
    }

    @GetMapping("/{busId}")
    public Bus getBusById(@PathVariable Long busId) {
        return busService.getBusById(busId).orElseThrow(() -> new RuntimeException("Bus not found with id " + busId));
    }

    @PutMapping("/{busId}")
    public Bus updateBus(@PathVariable Long busId, @RequestBody Bus busDetails) {
        return busService.updateBus(busId, busDetails);
    }

    @DeleteMapping("/{busId}")
    public void deleteBus(@PathVariable Long busId) {
        busService.deleteBus(busId);
    }
}
