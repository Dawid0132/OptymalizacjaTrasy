package com.example.mapauthrest.Service;

import com.example.databaseCore.Entities.Maps.Coordinates;
import com.example.databaseCore.Entities.Maps.CoordinatesForTrips;
import com.example.databaseCore.Entities.Maps.Trips;
import com.example.databaseCore.Entities.Maps.VerifyClickedCoordinates;
import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.Maps.Req.Coordinates_Req;
import com.example.databaseCore.Pojos.Maps.Req.Route.Route;
import com.example.databaseCore.Repositories.Maps.CoordinatesForTripsRepository;
import com.example.databaseCore.Repositories.Maps.CoordinatesRepository;
import com.example.databaseCore.Repositories.Maps.TripsRepository;
import com.example.databaseCore.Repositories.Maps.VerifyClickedCoordinatesRepository;
import com.example.databaseCore.Repositories.User.UserRepository;
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
    private final CoordinatesForTripsRepository coordinatesForTripsRepository;

    private final TripsRepository tripsRepository;

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public MapRestApiService(CoordinatesRepository coordinatesRepository, VerifyClickedCoordinatesRepository verifyClickedCoordinatesRepository, CoordinatesForTripsRepository coordinatesForTripsRepository, TripsRepository tripsRepository, UserRepository userRepository, RestTemplate restTemplate) {
        this.coordinatesRepository = coordinatesRepository;
        this.verifyClickedCoordinatesRepository = verifyClickedCoordinatesRepository;
        this.coordinatesForTripsRepository = coordinatesForTripsRepository;
        this.tripsRepository = tripsRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Object> getRoute(Long user_id) {

        Optional<User> user = userRepository.findById(user_id);

        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Coordinates> coordinates = user.get().getCoordinates();

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

        Optional<User> user = userRepository.findById(user_id);

        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Coordinates> coordinates = user.get().getCoordinates();


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

    public ResponseEntity<VerifyClickedCoordinates> updateClickedCoordinates(Long user_id, Coordinates_Req coordinates_req) {
        Optional<User> user = userRepository.findById(user_id);

        if (user.isPresent()) {
            try {
                User _user = user.get();
                VerifyClickedCoordinates verifyClickedCoordinates = new VerifyClickedCoordinates();
                verifyClickedCoordinates.setLongitude(coordinates_req.getLongitude());
                verifyClickedCoordinates.setLatitude(coordinates_req.getLatitude());
                _user.setVerifyClickedCoordinates(verifyClickedCoordinates);
                userRepository.save(_user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<VerifyClickedCoordinates> getClickedCoordinates(Long user_id, Coordinates_Req coordinates_req) {
        Optional<User> user = userRepository.findById(user_id);

        if (user.isPresent()) {
            User _user = user.get();

            VerifyClickedCoordinates clickedCoordinates = _user.getVerifyClickedCoordinates();
            List<Coordinates> coordinatesList = _user.getCoordinates();


            float epsilon = 0.00000001F;
            boolean key = true;

            if (!coordinatesList.isEmpty()) {
                for (Coordinates coordinates : coordinatesList) {
                    if (Math.abs(coordinates.getLatitude() - coordinates_req.getLatitude()) < epsilon && Math.abs(coordinates.getLongitude() - coordinates_req.getLongitude()) < epsilon) {
                        key = false;
                    }
                }
            }

            if (clickedCoordinates != null && key) {
                if (Math.abs(clickedCoordinates.getLatitude() - coordinates_req.getLatitude()) < epsilon && Math.abs(clickedCoordinates.getLongitude() - coordinates_req.getLongitude()) < epsilon) {
                    try {
                        Coordinates coordinates = new Coordinates();
                        coordinates.setLongitude(coordinates_req.getLongitude());
                        coordinates.setLatitude(coordinates_req.getLatitude());
                        _user.addCoordinates(coordinates);
                        userRepository.save(_user);
                        return ResponseEntity.ok(clickedCoordinates);
                    } catch (Exception e) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Coordinates> deleteCoordinates(Long user_id, List<Long> ids) {
        Optional<User> user = userRepository.findById(user_id);

        try {
            if (user.isPresent()) {
                User _user = user.get();
                _user.removeCoordinates(ids);
                userRepository.save(_user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Coordinates>> getAllCoordinates(Long user_id) {
        Optional<User> user = userRepository.findById(user_id);
        if (user.isPresent()) {
            List<Coordinates> coordinates = coordinatesRepository.findByUserId(user_id);
            return ResponseEntity.ok(coordinates);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public Long calculateEndDate(Float duration) {
        final float drivingLimitPerDayInSeconds = 9 * 3600;
        final float breakIntervalInSeconds = 4.5f * 3600;
        final float breakDurationInSeconds = 45 * 60;

        float totalTimeWithBreaks = 0f;
        float remainingDrivingTime = duration;

        while (remainingDrivingTime > 0) {
            float todayDriving = Math.min(remainingDrivingTime, drivingLimitPerDayInSeconds);
            int numberOfBreaks = (int) (todayDriving / breakIntervalInSeconds);

            float breaksToday = numberOfBreaks * breakDurationInSeconds;

            totalTimeWithBreaks += todayDriving + breaksToday;

            remainingDrivingTime -= todayDriving;
        }

        float maxTimePerDayIncludingBreaks = drivingLimitPerDayInSeconds + ((int) (drivingLimitPerDayInSeconds / breakIntervalInSeconds)) * breakDurationInSeconds;

        return (long) Math.ceil(totalTimeWithBreaks / maxTimePerDayIncludingBreaks);
    }

    public ResponseEntity<List<Trips>> getAllUnfinishedTrips(Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);

            if (user.isPresent()) {
                User _user = user.get();
                List<Trips> unfinishedTrips = new ArrayList<>();
                for (Trips trip : _user.getTrips()) {
                    if (!trip.getFinished()) {
                        unfinishedTrips.add(trip);
                    }
                }
                return ResponseEntity.ok(unfinishedTrips);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
