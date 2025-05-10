package com.example.mapauthrest.Service;

import com.example.mapauthrest.DB.Entities.Coordinates;
import com.example.mapauthrest.DB.Entities.VerifyClickedCoordinates;
import com.example.mapauthrest.DB.Repositories.CoordinatesRepository;
import com.example.mapauthrest.DB.Repositories.VerifyClickedCoordinatesRepository;
import com.example.mapauthrest.Pojo.Request.Coordinates_Req;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public ResponseEntity<VerifyClickedCoordinates> getClickedCoordinates(Long user_id, Coordinates_Req coordinates_req) {
        Optional<VerifyClickedCoordinates> clickedCoordinates = verifyClickedCoordinatesRepository.findByUserId(user_id);
        List<Coordinates> coordinatesList = coordinatesRepository.findAllByUserId(user_id);


        float epsilon = 0.00000001F;
        boolean key = true;

        if (!coordinatesList.isEmpty()) {
            for (Coordinates coordinates : coordinatesList) {
                if (Math.abs(coordinates.getLatitude() - coordinates_req.getLatitude()) < epsilon && Math.abs(coordinates.getLongitude() - coordinates_req.getLongitude()) < epsilon) {
                    key = false;
                }
            }
        }

        if (clickedCoordinates.isPresent() && key) {
            VerifyClickedCoordinates verifyClickedCoordinates = clickedCoordinates.get();
            if (Math.abs(verifyClickedCoordinates.getLatitude() - coordinates_req.getLatitude()) < epsilon && Math.abs(verifyClickedCoordinates.getLongitude() - coordinates_req.getLongitude()) < epsilon) {
                Coordinates coordinates = new Coordinates();
                try {
                    coordinates.setUserId(user_id);
                    coordinates.setLongitude(coordinates_req.getLongitude());
                    coordinates.setLatitude(coordinates_req.getLatitude());
                    coordinatesRepository.save(coordinates);
                    return ResponseEntity.ok(verifyClickedCoordinates);
                } catch (Exception e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<Coordinates>> getAllCoordinates(Long user_id) {
        List<Coordinates> coordinates = new ArrayList<Coordinates>(coordinatesRepository.findAllByUserId(user_id));
        return ResponseEntity.ok(coordinates);
    }

    @Transactional
    public ResponseEntity<Coordinates> deleteCoordinates(Long[] ids) {
        try {
            coordinatesRepository.deleteCoordinatesByIds(Arrays.asList(ids));

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
