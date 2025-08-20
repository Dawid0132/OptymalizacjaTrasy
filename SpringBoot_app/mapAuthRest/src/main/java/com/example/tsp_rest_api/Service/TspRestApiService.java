package com.example.tsp_rest_api.Service;


import com.example.databaseCore.Entities.Maps.*;
import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.Maps.Req.Coordinates_Req;
import com.example.databaseCore.Pojos.Maps.Req.Route.Route;
import com.example.databaseCore.Pojos.Maps.Req.SavedTrips.SavedTripReq;
import com.example.databaseCore.Pojos.Maps.Res.SummaryOfTrips;
import com.example.databaseCore.Repositories.Maps.*;
import com.example.databaseCore.Repositories.User.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class TspRestApiService {

    private final CoordinatesRepository coordinatesRepository;
    private final VerifyClickedCoordinatesRepository verifyClickedCoordinatesRepository;

    private final CoordinatesForTripsRepository coordinatesForTripsRepository;

    private final TripsRepository tripsRepository;

    private final MeasuringTimeRepository measuringTimeRepository;

    private final UserRepository userRepository;

    private final RestTemplate restTemplate;

    public TspRestApiService(CoordinatesRepository coordinatesRepository, VerifyClickedCoordinatesRepository verifyClickedCoordinatesRepository, CoordinatesForTripsRepository coordinatesForTripsRepository, TripsRepository tripsRepository, MeasuringTimeRepository measuringTimeRepository, UserRepository userRepository, RestTemplate restTemplate) {
        this.coordinatesRepository = coordinatesRepository;
        this.verifyClickedCoordinatesRepository = verifyClickedCoordinatesRepository;
        this.coordinatesForTripsRepository = coordinatesForTripsRepository;
        this.tripsRepository = tripsRepository;
        this.measuringTimeRepository = measuringTimeRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Object> getRoute(Long user_id) {
        Optional<User> user = userRepository.findById(user_id);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            List<Coordinates> coordinates = user.get().getCoordinates();
            if (coordinates.size() < 2) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            String url = "http://router.project-osrm.org/trip/v1/driving/" +
                    "{coordinates}?geometries={geometries}&overview={overview}" +
                    "&steps={steps}&annotations={annotations}";
            StringBuilder coordinatesStr = new StringBuilder();
            for (Coordinates coordinate : coordinates) {
                coordinatesStr.append(coordinate.getLongitude()).append(",").append(coordinate.getLatitude()).append(";");
            }
            if (!coordinatesStr.isEmpty()) {
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

    public ResponseEntity<Object> getTrip(Long user_id, Long trip_id) {

        Optional<Trips> trips = tripsRepository.findByIdAndUserId(trip_id, user_id);

        if (trips.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Trips _trips = trips.get();

            String url = "http://router.project-osrm.org/trip/v1/driving/{coordinates}?geometries={geometries}&overview={overview}&steps={steps}&annotations={annotations}";
            StringBuilder coordinatesStr = new StringBuilder();
            for (CoordinatesForTrips c : _trips.getCoordinates()) {
                coordinatesStr.append(c.getLongitude()).append(",").append(c.getLatitude()).append(";");
            }

            if (!coordinatesStr.isEmpty()) {
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

            UUID map_name = _trips.getMapName();

            Map<String, Object> result = new HashMap<>();
            result.put("osrm", response.getBody());
            result.put("map_name", map_name);

            return ResponseEntity.ok(result);
        } catch (HttpClientErrorException.BadRequest exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getLegs(Long userId, UUID mapName) {

        Optional<Trips> trips = tripsRepository.findByNameAndUserId(mapName, userId);

        if (trips.isPresent()) {
            try {
                Trips _trips = trips.get();

                String url = "http://router.project-osrm.org/trip/v1/driving/{coordinates}?geometries={geometries}&overview={overview}&steps={steps}&annotations={annotations}";
                StringBuilder coordinatesStr = new StringBuilder();
                for (CoordinatesForTrips c : _trips.getCoordinates()) {
                    coordinatesStr.append(c.getLongitude()).append(",").append(c.getLatitude()).append(";");
                }

                if (!coordinatesStr.isEmpty()) {
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


                return ResponseEntity.ok(response.getBody().getTrips().getFirst().getLegs());
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<VerifyClickedCoordinates> updateClickedCoordinates(Long user_id, Coordinates_Req coordinates_req) {

        Optional<User> user = userRepository.findById(user_id);

        if (user.isPresent()) {
            try {
                User _user = user.get();
                VerifyClickedCoordinates verifyClickedCoordinates = _user.getVerifyClickedCoordinates();

                if (verifyClickedCoordinates == null) {
                    verifyClickedCoordinates = new VerifyClickedCoordinates();
                    verifyClickedCoordinates.setUser(_user);
                }

                verifyClickedCoordinates.setLatitude(coordinates_req.getLatitude());
                verifyClickedCoordinates.setLongitude(coordinates_req.getLongitude());


                _user.setVerifyClickedCoordinates(verifyClickedCoordinates);

                userRepository.save(_user);

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<Coordinates>> getClickedCoordinates(Long user_id, Coordinates_Req coordinates_req) {

        Optional<User> user = userRepository.findById(user_id);

        if (user.isPresent()) {
            User _user = user.get();

            VerifyClickedCoordinates clickedCoordinates = _user.getVerifyClickedCoordinates();
            List<Coordinates> coordinatesList = _user.getCoordinates();


            float epsilon = 0.00000001F;
            boolean key = true;

            if (!coordinatesList.isEmpty()) {
                for (Coordinates coordinates : coordinatesList) {
                    if (Math.abs(coordinates.getLatitude() - coordinates_req.getLatitude())
                            < epsilon && Math.abs(coordinates.getLongitude() - coordinates_req.getLongitude()) < epsilon) {
                        key = false;
                    }
                }
            }

            if (clickedCoordinates != null && key) {
                if (Math.abs(clickedCoordinates.getLatitude() - coordinates_req.getLatitude())
                        < epsilon && Math.abs(clickedCoordinates.getLongitude() - coordinates_req.getLongitude()) < epsilon) {
                    try {
                        Coordinates coordinates = new Coordinates();
                        coordinates.setLongitude(coordinates_req.getLongitude());
                        coordinates.setLatitude(coordinates_req.getLatitude());
                        _user.addCoordinates(coordinates);
                        userRepository.save(_user);
                        return ResponseEntity.ok(_user.getCoordinates());
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

    public ResponseEntity<List<Coordinates>> deleteCoordinates(Long user_id, List<Long> ids) {

        Optional<User> user = userRepository.findById(user_id);

        try {
            if (user.isPresent()) {
                User _user = user.get();
                _user.removeCoordinates(ids);
                userRepository.save(_user);
                return ResponseEntity.ok(_user.getCoordinates());
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Coordinates>> getAllCoordinates(Long user_id) {

        Optional<List<Coordinates>> coordinatesList = coordinatesRepository.findByUserId(user_id);

        return coordinatesList.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public Long calculateEndDate(Long duration) {
        final long drivingLimitPerDayInSeconds = 9 * 3600 * 1000;

        if (duration <= drivingLimitPerDayInSeconds) {
            return 0L;
        }
        return duration / drivingLimitPerDayInSeconds;
    }

    public ResponseEntity<Trips> addTrip(Long userId, SavedTripReq savedTrips) {

        try {
            Optional<User> user = userRepository.findById(userId);

            if (user.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (savedTrips.getStartDate().isBefore(LocalDate.now()) || savedTrips.getStartDate().isAfter(LocalDate.now().plusMonths(1))) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            User _user = user.get();

            Trips trips = new Trips();
            trips.setStartDate(savedTrips.getStartDate());
            trips.setEndDate(savedTrips.getStartDate().plusDays(calculateEndDate(savedTrips.getTrip().getDuration())));
            trips.setDistance(savedTrips.getTrip().getDistance());
            trips.setDuration(savedTrips.getTrip().getDuration());
            trips.setCreatedAt(LocalDate.now());
            IsFinished isFinished = new IsFinished();
            isFinished.setTrips(trips);
            trips.setIsFinished(isFinished);


            for (Coordinates_Req coordinates1 : savedTrips.getTrip().getCoordinates()) {
                CoordinatesForTrips coordinatesForTrips1 = new CoordinatesForTrips();
                coordinatesForTrips1.setLatitude(coordinates1.getLatitude());
                coordinatesForTrips1.setLongitude(coordinates1.getLongitude());
                trips.addCoordinatesForTrips(coordinatesForTrips1);
            }

            float epsilon = 0.00000001F;

            for (Trips t : _user.getTrips()) {
                Integer count = 0;
                for (CoordinatesForTrips c : t.getCoordinates()) {
                    for (CoordinatesForTrips c1 : trips.getCoordinates()) {
                        if (!t.getIsFinished().getFinished()) {
                            if (Math.abs(c.getLatitude() - c1.getLatitude()) < epsilon && Math.abs(c.getLongitude() - c1.getLongitude()) < epsilon) {
                                count++;
                                break;
                            }
                        }
                    }
                }

                if (count.equals(trips.getCoordinates().size())) {
                    return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                }
            }

            MeasuringTime measuringTime = new MeasuringTime();
            measuringTime.setTrips(trips);
            trips.setMeasuringTime(measuringTime);
            _user.addTrips(trips);
            _user.getCoordinates().clear();
            userRepository.save(_user);

            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            log.error("Deserialization error: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Trips>> getAllUnfinishedTrips(Long userId) {

        Optional<List<Trips>> tripsList = tripsRepository.getAllByFinishedCondition(userId, false);

        try {
            return tripsList.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Trips>> deleteTrip(Long userId, Long trip_id) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            try {
                User _user = user.get();
                List<Trips> trips = _user.removeTrips(trip_id);
                userRepository.save(_user);
                return ResponseEntity.ok(trips);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Boolean> startDriving(Long userId, UUID mapName) {

        try {
            Optional<Trips> trips = tripsRepository.findByNameAndUserId(mapName, userId);
            if (trips.isPresent()) {
                Trips _trips = trips.get();
                MeasuringTime measuringTime = _trips.getMeasuringTime();
                if (measuringTime.getDriving()) {
                    Long total = Duration.between(measuringTime.getChangingStatus(), LocalDateTime.now()).toMillis();
                    measuringTime.setTotal(measuringTime.getTotal() + total);
                } else {
                    measuringTime.setChangingStatus(LocalDateTime.now());
                }

                measuringTime.setDriving(!measuringTime.getDriving());
                tripsRepository.save(_trips);
                return ResponseEntity.ok(measuringTime.getDriving());
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Boolean> measuringTimeStatus(Long userId, UUID mapName) {
        try {
            Optional<Trips> trips = tripsRepository.findByNameAndUserId(mapName, userId);
            return trips.map(value -> ResponseEntity.ok(value.getMeasuringTime().getDriving())).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Trips>> finishTrip(Long userId, Long trip_id) {

        try {
            Optional<Trips> trips = tripsRepository.findByIdAndUserId(trip_id, userId);

            if (trips.isPresent()) {
                Trips _trips = trips.get();
                User user = _trips.getUser();

                if (_trips.getMeasuringTime().getDriving()) {
                    return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                }

                _trips.getIsFinished().setFinished(Boolean.TRUE);
                _trips.getIsFinished().setFinishedAt(LocalDate.now());

                tripsRepository.save(_trips);

                Optional<List<Trips>> tripsList = tripsRepository.getAllByFinishedCondition(user.getId(), Boolean.FALSE);

                return tripsList.map(ResponseEntity::ok).orElse(null);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<SummaryOfTrips>> getAllFinishedTrips(Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                User _user = user.get();
                Optional<List<Trips>> tripsList = tripsRepository.getAllByFinishedCondition(_user.getId(), Boolean.TRUE);
                if (tripsList.isPresent()) {
                    List<SummaryOfTrips> summaryOfTripsList = new ArrayList<>();
                    for (Trips t : tripsList.get()) {
                        SummaryOfTrips summaryOfTrips = new SummaryOfTrips();
                        summaryOfTrips.setStartDateOfTrip(t.getStartDate());
                        summaryOfTrips.setEndDateOfTrip(t.getEndDate());
                        summaryOfTrips.setRealisedEndDateOfTrip(t.getIsFinished().getFinishedAt());
                        summaryOfTrips.setDistance(t.getDistance());
                        summaryOfTrips.setDuration(t.getDuration());
                        summaryOfTrips.setRealisedDuration(t.getMeasuringTime().getTotal());
                        summaryOfTripsList.add(summaryOfTrips);
                    }
                    return ResponseEntity.ok(summaryOfTripsList);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
