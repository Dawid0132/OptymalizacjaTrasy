package com.example.mapauthrest.Service;

import com.example.mapauthrest.DB.Entities.VerifyClickedCoordinates;
import com.example.mapauthrest.DB.Repositories.CoordinatesRepository;
import com.example.mapauthrest.DB.Repositories.VerifyClickedCoordinatesRepository;
import com.example.mapauthrest.Pojo.Request.Coordinates_Req;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class MapRestApiService {

    private final CoordinatesRepository coordinatesRepository;
    private final VerifyClickedCoordinatesRepository verifyClickedCoordinatesRepository;
    private final RestTemplate restTemplate;

    public MapRestApiService(CoordinatesRepository coordinatesRepository, VerifyClickedCoordinatesRepository verifyClickedCoordinatesRepository, RestTemplate restTemplate) {
        this.coordinatesRepository = coordinatesRepository;
        this.verifyClickedCoordinatesRepository = verifyClickedCoordinatesRepository;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<VerifyClickedCoordinates> updateClickedCoordinates(Long user_id, Coordinates_Req coordinates_req) {
        Optional<VerifyClickedCoordinates> clickedCoordinates = verifyClickedCoordinatesRepository.findByUserId(user_id);

        if (clickedCoordinates.isPresent()) {
            try {
                VerifyClickedCoordinates verifyClickedCoordinates = clickedCoordinates.get();
                verifyClickedCoordinates.setLongitude(coordinates_req.getLongitude());
                verifyClickedCoordinates.setLatitude(coordinates_req.getLatitude());
                verifyClickedCoordinatesRepository.save(verifyClickedCoordinates);
                return ResponseEntity.ok(verifyClickedCoordinates);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            try {
                VerifyClickedCoordinates verifyClickedCoordinates = new VerifyClickedCoordinates();
                verifyClickedCoordinates.setUserId(user_id);
                verifyClickedCoordinates.setLongitude(coordinates_req.getLongitude());
                verifyClickedCoordinates.setLatitude(coordinates_req.getLatitude());
                verifyClickedCoordinatesRepository.save(verifyClickedCoordinates);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }

}
