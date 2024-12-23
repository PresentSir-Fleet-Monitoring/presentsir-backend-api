package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.Bus;
import com.ranjit.ps.model.Location;
import com.ranjit.ps.model.dto.UserLocation;
import com.ranjit.ps.service.BusService;
import com.ranjit.ps.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class RestPublicControlller {
    @Autowired
    private BusService busService;
    @Autowired
    private LocationService locationService;

    @Autowired
    public RestPublicControlller(BusService busService) {
        this.busService = busService;
    }

    @GetMapping("/buses")
    public List<Bus> getAllBuses() {
        return busService.getAllBuses();
    }

    @GetMapping("/location")
    public ResponseEntity<Location> getAllBuses(@RequestParam long busId) {
        Location location = new Location();

        try {
            // Call the service to broadcast the location for the given busId
            UserLocation userLocation = locationService.broadcastToBus(busId);

            // Check if the userLocation is null (in case no location was found)
            if (userLocation == null) {
                String errorMsg = "Location data not found for busId: " + busId;
                System.err.println(errorMsg);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
            }

            // Set the values to the location object
            location.setLongitude(userLocation.getLongitude());
            location.setLatitude(userLocation.getLatitude());
            location.setBusId(userLocation.getBusId());

            // Return the location data with a 200 OK status
            return ResponseEntity.ok(location);

        } catch (Exception e) {
            // Log the exception
            String errorMsg = "Error occurred while retrieving location for busId: " + busId;
            System.err.println(errorMsg);
            e.printStackTrace();

            // Return a 500 Internal Server Error with a message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
