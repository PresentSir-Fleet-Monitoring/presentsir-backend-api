package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.Location;
import com.ranjit.ps.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class RestLocationController {

    private final LocationService locationService;

   @Autowired
   public RestLocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/{busId}/locations")
    public String addLocation(@PathVariable String busId, @RequestParam double latitude, @RequestParam double longitude) {
        locationService.addLocationToBus(busId, latitude, longitude);
        return "Added location to bus " + busId;
    }

//    USER FETCH BUS LOCATION FROM Q-SERVICE

    @GetMapping("/{busId}/locations")
    public Location getLocation(@PathVariable String busId){
        return locationService.getLocation(busId);
    }

}
