package com.example.servicemapauthrest.Controller;

import com.example.servicemapauthrest.Pojo.Coordinates;
import com.example.servicemapauthrest.Pojo.SavedTrips.SavedTripReq;
import com.example.servicemapauthrest.Service.ServiceMapAuthRestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/map/v1")
public class ServiceController {

    private final ServiceMapAuthRestService serviceMapAuthRestService;

    public ServiceController(ServiceMapAuthRestService serviceMapAuthRestService) {
        this.serviceMapAuthRestService = serviceMapAuthRestService;
    }

    @GetMapping(path = "/getRoute/{user_id}")
    public ResponseEntity<Object> generateRoute(@PathVariable("user_id") Long user_id, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.generateRoute(user_id, authToken));
    }

    @GetMapping(path = "/getRoute/legs/{user_id}")
    public ResponseEntity<Object> getLegs(@PathVariable("user_id") Long user_id, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.getLegs(user_id, authToken));
    }

    @PutMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<Coordinates> verifyLastClickedCoordinates(@PathVariable("user_id") Long user_id, @RequestBody Coordinates coordinates, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.verifyLastClickedCoordinates(user_id, coordinates, authToken));
    }

    @PostMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<Coordinates> addCoordinates(@PathVariable("user_id") Long user_id, @RequestBody Coordinates coordinates, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.addCoordinates(user_id, coordinates, authToken));
    }

    @GetMapping(path = "/coordinates/{user_id}")
    public ResponseEntity<List<Coordinates>> getListOfCoordinates(@PathVariable("user_id") Long user_id, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.getListOfCoordinates(user_id, authToken));
    }

    @GetMapping(path = "/deleteCoordinates/{user_id}")
    public ResponseEntity<Void> deleteCoordinates(@PathVariable("user_id") Long user_id, @RequestBody List<Long> ids, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.deleteCoordinates(user_id, ids, authToken));
    }

    @PostMapping(path = "/trips/{user_id}")
    public ResponseEntity<Object> addTrips(@PathVariable("user_id") Long user_id, @RequestBody SavedTripReq savedTripReq, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.addTrips(user_id, savedTripReq, authToken));
    }

    @GetMapping(path = "/trips/delete/{user_id}")
    public ResponseEntity<Object> deleteTrips(@PathVariable("user_id") Long user_id, @RequestParam Long trip_id, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.deleteTrips(user_id, trip_id, authToken));
    }

    @GetMapping(path = "/trips/unfinished/{user_id}")
    public ResponseEntity<Object> getAllUnfinishedTrips(@PathVariable("user_id") Long user_id, @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(serviceMapAuthRestService.getAllUnfinishedTrips(user_id, authToken));
    }
}
