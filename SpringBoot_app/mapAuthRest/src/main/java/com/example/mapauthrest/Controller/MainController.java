package com.example.mapauthrest.Controller;


import com.example.databaseCore.Entities.Maps.Coordinates;
import com.example.databaseCore.Entities.Maps.VerifyClickedCoordinates;
import com.example.databaseCore.Pojos.Maps.Req.Coordinates_Req;
import com.example.mapauthrest.Service.MapRestApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/rest/map/v2")
public class MainController {
    private final MapRestApiService mapRestApiService;

    public MainController(MapRestApiService mapRestApiService) {
        this.mapRestApiService = mapRestApiService;
    }

    @GetMapping(path = "/getRoute/{user_id}")
    public ResponseEntity<Object> getRoute(@PathVariable("user_id") Long user_id) {
        return mapRestApiService.getRoute(user_id);
    }

    @GetMapping(path = "/getRoute/legs/{user_id}")
    public ResponseEntity<Object> getLegs(@PathVariable("user_id") Long user_id) {
        return mapRestApiService.getLegs(user_id);
    }

    @PutMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<VerifyClickedCoordinates> updateCoordinatesClicked(@PathVariable("user_id") Long user_id, @RequestBody Coordinates_Req coordinates_req) {
        return mapRestApiService.updateClickedCoordinates(user_id, coordinates_req);
    }

    @PostMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<VerifyClickedCoordinates> getCoordinatesClicked(@PathVariable("user_id") Long user_id, @RequestBody Coordinates_Req coordinates_req) {
        return mapRestApiService.getClickedCoordinates(user_id, coordinates_req);
    }

    @DeleteMapping(path = "/coordinates/{user_id}")
    public ResponseEntity<Coordinates> deleteCoordinates(@PathVariable("user_id") Long user_id, @RequestBody List<Long> ids) {
        return mapRestApiService.deleteCoordinates(user_id, ids);
    }

    @GetMapping(path = "/coordinates/{user_id}")
    public ResponseEntity<List<Coordinates>> getAllCoordinates(@PathVariable("user_id") Long user_id) {
        return mapRestApiService.getAllCoordinates(user_id);
    }


}
