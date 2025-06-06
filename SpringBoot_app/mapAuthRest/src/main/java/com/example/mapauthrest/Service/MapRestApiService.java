package com.example.mapauthrest.Service;

import com.example.mapauthrest.DB.Entities.Coordinates;
import com.example.mapauthrest.DB.Entities.VerifyClickedCoordinates;
import com.example.mapauthrest.DB.Repositories.CoordinatesRepository;
import com.example.mapauthrest.DB.Repositories.VerifyClickedCoordinatesRepository;
import com.example.mapauthrest.Pojo.Request.Coordinates_Req;
import com.example.mapauthrest.Pojo.Route.Route;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    public ResponseEntity<Object> getRoute(Long user_id) {

        List<Coordinates> coordinates = new ArrayList<Coordinates>(coordinatesRepository.findAllByUserId(user_id));

        if (coordinates.size() < 2) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            String url = "http://router.project-osrm.org/trip/v1/driving/{coordinates}?geometries={geometries}&overview={overview}&steps={steps}&annotations={annotations}";
            StringBuilder coordinatesStr = new StringBuilder();
            for (Coordinates coordinate : coordinates) {
                coordinatesStr.append(coordinate.getLongitude()).append(",").append(coordinate.getLatitude()).append(";");
            }

            if (coordinatesStr.length() > 0) {
                coordinatesStr.setLength(coordinatesStr.length() - 1);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            Map<String, String> map = new HashMap<>();
            map.put("coordinates", coordinatesStr.toString());
            map.put("geometries", "geojson");
            map.put("overview", "full");
            map.put("steps", "true");
            map.put("annotations", "true");

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, map);

            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.BadRequest exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<Object> getLegs(Long user_id) {

        List<Coordinates> coordinates = new ArrayList<Coordinates>(coordinatesRepository.findAllByUserId(user_id));

        if (coordinates.size() < 2) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            String url = "http://router.project-osrm.org/trip/v1/driving/{coordinates}?geometries={geometries}&overview={overview}&steps={steps}&annotations={annotations}";
            StringBuilder coordinatesStr = new StringBuilder();
            for (Coordinates coordinate : coordinates) {
                coordinatesStr.append(coordinate.getLongitude()).append(",").append(coordinate.getLatitude()).append(";");
            }

            if (coordinatesStr.length() > 0) {
                coordinatesStr.setLength(coordinatesStr.length() - 1);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            Map<String, String> map = new HashMap<>();
            map.put("coordinates", coordinatesStr.toString());
            map.put("geometries", "geojson");
            map.put("overview", "full");
            map.put("steps", "true");
            map.put("annotations", "true");

            ResponseEntity<Route> response = restTemplate.exchange(url, HttpMethod.GET, entity, Route.class, map);

            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.BadRequest exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
