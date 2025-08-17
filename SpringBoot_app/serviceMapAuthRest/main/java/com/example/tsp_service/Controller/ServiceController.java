package com.example.tsp_service.Controller;


import com.example.tsp_service.Pojos.Coordinates;
import com.example.tsp_service.Pojos.SavedTrips.SavedTripReq;
import com.example.tsp_service.TspService.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/map/v1")
public class ServiceController {

    private final AppService appService;

    public ServiceController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping(path = "/{user_id}/getRoute")
    public ResponseEntity<Object> generateRoute(@PathVariable("user_id") Long user_id) {
        return ResponseEntity.ok(appService.generateRoute(user_id));
    }

    @GetMapping(path = "/{user_id}/getRoute/trip")
    public ResponseEntity<Object> getTrip(@PathVariable("user_id") Long user_id, @RequestParam Long trip_id) {
        return ResponseEntity.ok(appService.getTrip(user_id, trip_id));
    }

    @GetMapping(path = "/{user_id}/getRoute/legs")
    public ResponseEntity<Object> getLegs(@PathVariable("user_id") Long user_id, @RequestParam UUID map_name) {
        return ResponseEntity.ok(appService.getLegs(user_id, map_name));
    }

    @PutMapping(path = "/{user_id}/coordinatesVerify")
    public ResponseEntity<Coordinates> verifyLastClickedCoordinates(@PathVariable("user_id") Long user_id, @RequestBody Coordinates coordinates) {
        return ResponseEntity.ok(appService.verifyLastClickedCoordinates(user_id, coordinates));
    }

    @PostMapping(path = "/{user_id}/coordinatesVerify")
    public ResponseEntity<List<Coordinates>> addCoordinates(@PathVariable("user_id") Long user_id, @RequestBody Coordinates coordinates) {
        return ResponseEntity.ok(appService.addCoordinates(user_id, coordinates));
    }

    @GetMapping(path = "/{user_id}/coordinates")
    public ResponseEntity<List<Coordinates>> getListOfCoordinates(@PathVariable("user_id") Long user_id) {
        return ResponseEntity.ok(appService.getListOfCoordinates(user_id));
    }

    @GetMapping(path = "/{user_id}/deleteCoordinates")
    public ResponseEntity<List<Coordinates>> deleteCoordinates(@PathVariable("user_id") Long user_id, @RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(appService.deleteCoordinates(user_id, ids));
    }

    @PostMapping(path = "/{user_id}/trips")
    public ResponseEntity<Object> addTrips(@PathVariable("user_id") Long user_id, @RequestBody SavedTripReq savedTripReq) {
        return ResponseEntity.ok(appService.addTrips(user_id, savedTripReq));
    }

    @GetMapping(path = "/{user_id}/trips/delete")
    public ResponseEntity<Object> deleteTrips(@PathVariable("user_id") Long user_id, @RequestParam Long trip_id) {
        return ResponseEntity.ok(appService.deleteTrips(user_id, trip_id));
    }

    @GetMapping(path = "/{user_id}/trips/unfinished")
    public ResponseEntity<Object> getAllUnfinishedTrips(@PathVariable("user_id") Long user_id) {
        return ResponseEntity.ok(appService.getAllUnfinishedTrips(user_id));
    }

    @GetMapping(path = "/{user_id}/trips/finish")
    public ResponseEntity<Object> finishTrip(@PathVariable("user_id") Long user_id, @RequestParam Long trip_id) {
        return ResponseEntity.ok(appService.finishTrip(user_id, trip_id));
    }

    @GetMapping(path = "/{user_id}/trips/startDriving")
    public ResponseEntity<Boolean> startDriving(@PathVariable("user_id") Long user_id, @RequestParam UUID map_name) {
        return ResponseEntity.ok(appService.startDriving(user_id, map_name));
    }

    @GetMapping(path = "/{user_id}/trips/measuringTime/status")
    public ResponseEntity<Boolean> measuringTimeStatus(@PathVariable("user_id") Long user_id, @RequestParam UUID map_name) {
        return ResponseEntity.ok(appService.measuringTimeStatus(user_id, map_name));
    }

    @GetMapping(path = "/{user_id}/trips/finished")
    public ResponseEntity<Object> getAllFinishedTrips(@PathVariable("user_id") Long user_id) {
        return ResponseEntity.ok(appService.getAllFinishedTrips(user_id));
    }
}
