package com.example.mapauthrest.Controller;

import com.example.mapauthrest.DB.Entities.Coordinates;
import com.example.mapauthrest.DB.Entities.VerifyClickedCoordinates;
import com.example.mapauthrest.Pojo.Request.Coordinates_Req;
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

    @PutMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<VerifyClickedCoordinates> updateCoordinatesClicked(@PathVariable("user_id") Long user_id, @RequestBody Coordinates_Req coordinates_req) {
        return mapRestApiService.updateClickedCoordinates(user_id, coordinates_req);
    }

    @PostMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<VerifyClickedCoordinates> getCoordinatesClicked(@PathVariable("user_id") Long user_id, @RequestBody Coordinates_Req coordinates_req) {
        return mapRestApiService.getClickedCoordinates(user_id, coordinates_req);
    }

    @GetMapping(path = "/coordinates/{user_id}")
    public ResponseEntity<List<Coordinates>> getAllCoordinates(@PathVariable("user_id") Long user_id) {
        return mapRestApiService.getAllCoordinates(user_id);
    }

    @DeleteMapping(path = "/coordinates/{ids}")
    public ResponseEntity<Coordinates> deleteCoordinates(@PathVariable("ids") Long[] ids) {
        return mapRestApiService.deleteCoordinates(ids);
    }

}
