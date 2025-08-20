package com.example.tsp_rest_api;

import com.example.databaseCore.Entities.Maps.Coordinates;
import com.example.databaseCore.Entities.Maps.CoordinatesForTrips;
import com.example.databaseCore.Entities.Maps.Trips;
import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.Maps.Req.Route.Route;
import com.example.databaseCore.Pojos.Maps.Req.Route.Trip;
import com.example.databaseCore.Repositories.Maps.TripsRepository;
import com.example.databaseCore.Repositories.User.UserRepository;
import com.example.tsp_rest_api.Service.TspRestApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExternalApiTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripsRepository tripsRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TspRestApiService tspRestApiService;


    @Test
    void testGetRoute_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = tspRestApiService.getRoute(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetRoute_NotEnoughCoordinates() {
        Long userId = 1L;
        User user = new User();
        user.setCoordinates(List.of(new Coordinates()));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = tspRestApiService.getRoute(userId);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }

    // Test 3: getRoute() - sukces
    @Test
    void testGetRoute_Success() {
        Long userId = 1L;
        Coordinates c1 = new Coordinates();
        c1.setLatitude(50.00F);
        c1.setLongitude(20.00F);
        Coordinates c2 = new Coordinates();
        c2.setLatitude(51.00F);
        c2.setLongitude(21.00F);
        User user = new User();
        user.setCoordinates(List.of(c1, c2));
        Map<String, String> params = Map.of(
                "coordinates", "20.0,50.0;21.0,51.0",
                "geometries", "geojson",
                "overview", "full",
                "steps", "true",
                "annotations", "true"
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                eq(params))
        ).thenReturn(new ResponseEntity<>(Map.of("mock", "data"), HttpStatus.OK));
        ResponseEntity<Object> response = tspRestApiService.getRoute(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("mock", "data"), response.getBody());
    }

    // Test 4: getTrip() - brak tripa
    @Test
    void testGetTrip_TripNotFound() {
        when(tripsRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = tspRestApiService.getTrip(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Test 5: getTrip() - sukces
    @Test
    void testGetTrip_Success() {
        Trips trip = new Trips();
        CoordinatesForTrips c1 = new CoordinatesForTrips();
        c1.setLatitude(50.00F);
        c1.setLongitude(20.00F);
        CoordinatesForTrips c2 = new CoordinatesForTrips();
        c2.setLatitude(51.00F);
        c2.setLongitude(21.00F);
        trip.setCoordinates(List.of(c1, c2));
        Map<String, Object> mockBody = Map.of("code", "Ok");
        when(tripsRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(trip));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        )).thenReturn(new ResponseEntity<>(mockBody, HttpStatus.OK));
        ResponseEntity<Object> response = tspRestApiService.getTrip(1L, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> result = (Map<String, Object>) response.getBody();
        assertEquals(mockBody, result.get("osrm"));
        assertEquals(trip.getMapName(), result.get("map_name"));
    }

    // Test 6: getLegs() - trip nie istnieje
    @Test
    void testGetLegs_TripNotFound() {
        when(tripsRepository.findByNameAndUserId(any(), any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = tspRestApiService.getLegs(1L, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Test 7: getLegs() - sukces
    @Test
    void testGetLegs_Success() {
        Trips trip = new Trips();
        CoordinatesForTrips c1 = new CoordinatesForTrips();
        c1.setLatitude(50.00F);
        c1.setLongitude(20.00F);
        CoordinatesForTrips c2 = new CoordinatesForTrips();
        c2.setLatitude(51.00F);
        c2.setLongitude(21.00F);
        trip.setCoordinates(List.of(c1, c2));
        Trip tripObj = new Trip();
        tripObj.setLegs(List.of("leg1", "leg2"));

        Route route = new Route();
        route.setTrips(List.of(tripObj));

        when(tripsRepository.findByNameAndUserId(any(), any())).thenReturn(Optional.of(trip));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Route.class),
                anyMap()
        )).thenReturn(new ResponseEntity<>(route, HttpStatus.OK));

        ResponseEntity<Object> response = tspRestApiService.getLegs(1L, UUID.randomUUID());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of("leg1", "leg2"), response.getBody());
    }


}
