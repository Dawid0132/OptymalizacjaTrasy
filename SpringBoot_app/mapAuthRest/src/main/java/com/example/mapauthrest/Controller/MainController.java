package com.example.mapauthrest.Controller;

import com.example.mapauthrest.DB.Entities.VerifyClickedCoordinates;
import com.example.mapauthrest.Pojo.Request.Coordinates_Req;
import com.example.mapauthrest.Service.MapRestApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/rest/map/v2")
public class MainController {
    private final MapRestApiService mapRestApiService;

    public MainController(MapRestApiService mapRestApiService) {
        this.mapRestApiService = mapRestApiService;
    }

    @PutMapping(path = "/coordinatesVerify/{user_id}")
    public ResponseEntity<VerifyClickedCoordinates> updateCoordinatesClicked(@PathVariable("user_id") Long user_id, @RequestBody Coordinates_Req coordinates_req) {
        return mapRestApiService.updateClickedCoordinates(user_id, coordinates_req);
    }


}
