package com.example.tsp_rest_api.Controller;


import com.example.databaseCore.Entities.Maps.Coordinates;
import com.example.databaseCore.Entities.Maps.Trips;
import com.example.databaseCore.Entities.Maps.VerifyClickedCoordinates;
import com.example.databaseCore.Pojos.Maps.Req.Coordinates_Req;
import com.example.databaseCore.Pojos.Maps.Req.SavedTrips.SavedTripReq;
import com.example.databaseCore.Pojos.Maps.Res.SummaryOfTrips;
import com.example.tsp_rest_api.Service.TspRestApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/rest/map/v2")
public class MainController {
    private final TspRestApiService tspRestApiService;

    public MainController(TspRestApiService tspRestApiService) {
        this.tspRestApiService = tspRestApiService;
    }

    @GetMapping(path = "/getRoute/{user_id}")
    public ResponseEntity<Object> getRoute(@PathVariable("user_id") Long user_id) {
        return tspRestApiService.getRoute(user_id);
    }

    @GetMapping(path = "/getRoute/trip/{user_id}")
    public ResponseEntity<Object> getTrip(@PathVariable("user_id") Long user_id, @RequestParam Long trip_id) {
        return tspRestApiService.getTrip(user_id, trip_id);
    }

    @GetMapping(path = "/getRoute/legs/{user_id}")
    public ResponseEntity<Object> getLegs(@PathVariable("user_id") Long user_id, @RequestParam UUID map_name) {
        return tspRestApiService.getLegs(user_id, map_name);
    }

    @PutMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<VerifyClickedCoordinates> updateCoordinatesClicked(@PathVariable("user_id") Long user_id, @RequestBody Coordinates_Req coordinates_req) {
        return tspRestApiService.updateClickedCoordinates(user_id, coordinates_req);
    }

    @PostMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<List<Coordinates>> getCoordinatesClicked(@PathVariable("user_id") Long user_id, @RequestBody Coordinates_Req coordinates_req) {
        return tspRestApiService.getClickedCoordinates(user_id, coordinates_req);
    }

    @DeleteMapping(path = "/coordinates/{user_id}")
    public ResponseEntity<List<Coordinates>> deleteCoordinates(@PathVariable("user_id") Long user_id, @RequestBody List<Long> ids) {
        return tspRestApiService.deleteCoordinates(user_id, ids);
    }

    @GetMapping(path = "/coordinates/{user_id}")
    public ResponseEntity<List<Coordinates>> getAllCoordinates(@PathVariable("user_id") Long user_id) {
        return tspRestApiService.getAllCoordinates(user_id);
    }

    @PostMapping(path = "/trips/{user_id}")
    public ResponseEntity<Trips> addTrip(@PathVariable("user_id") Long user_id, @RequestBody SavedTripReq savedTrips) {
        return tspRestApiService.addTrip(user_id, savedTrips);
    }

    @DeleteMapping("/trips/delete/{user_id}")
    public ResponseEntity<List<Trips>> deleteTrip(@PathVariable("user_id") Long user_id, @RequestParam Long trip_id) {
        return tspRestApiService.deleteTrip(user_id, trip_id);
    }

    @PutMapping("/trips/startDriving/{user_id}")
    public ResponseEntity<Boolean> startDriving(@PathVariable("user_id") Long user_id, @RequestParam UUID map_name) {
        return tspRestApiService.startDriving(user_id, map_name);
    }

    @GetMapping("/trips/measuringTime/status/{user_id}")
    public ResponseEntity<Boolean> measuringTimeStatus(@PathVariable("user_id") Long user_id, @RequestParam UUID map_name) {
        return tspRestApiService.measuringTimeStatus(user_id, map_name);
    }

    @PostMapping("/trips/finish/{user_id}")
    public ResponseEntity<List<Trips>> finishTrip(@PathVariable("user_id") Long user_id, @RequestParam Long trip_id) {
        return tspRestApiService.finishTrip(user_id, trip_id);
    }

    @GetMapping(path = "/trips/unfinished/{user_id}")
    public ResponseEntity<List<Trips>> getAllUnfinishedTrips(@PathVariable("user_id") Long user_id) {
        return tspRestApiService.getAllUnfinishedTrips(user_id);
    }

    @GetMapping(path = "/trips/finished/{user_id}")
    public ResponseEntity<List<SummaryOfTrips>> getAllFinishedTrips(@PathVariable("user_id") Long user_id) {
        return tspRestApiService.getAllFinishedTrips(user_id);
    }
}
