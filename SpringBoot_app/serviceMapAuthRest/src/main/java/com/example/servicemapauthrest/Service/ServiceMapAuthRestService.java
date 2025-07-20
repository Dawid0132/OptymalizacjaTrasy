package com.example.servicemapauthrest.Service;

import com.example.servicemapauthrest.Pojo.Coordinates;
import com.example.servicemapauthrest.Pojo.SavedTrips.SavedTripReq;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceMapAuthRestService {
    private final RestTemplate restTemplate;

    public ServiceMapAuthRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object generateRoute(Long user_id, String authToken) {
        String url = "http://localhost:8080/rest/map/v2/getRoute/{user_id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object getLegs(Long user_id, String authToken) {
        String url = "http://localhost:8080/rest/map/v2/getRoute/legs/{user_id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Coordinates verifyLastClickedCoordinates(Long user_id, Coordinates coordinates, String authToken) {
        String url = "http://localhost:8080/rest/map/v2/coordinatesVerify/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<Coordinates> entity = new HttpEntity<>(coordinates, headers);

        ResponseEntity<Coordinates> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Coordinates.class, user_id);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Coordinates addCoordinates(Long user_id, Coordinates coordinates, String authToken) {
        String url = "http://localhost:8080/rest/map/v2/coordinatesVerify/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<Coordinates> entity = new HttpEntity<>(coordinates, headers);

        ResponseEntity<Coordinates> response = restTemplate.exchange(url, HttpMethod.POST, entity, Coordinates.class, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public List<Coordinates> getListOfCoordinates(Long user_id, String authToken) {
        String url = "http://localhost:8080/rest/map/v2/coordinates/{user_id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Coordinates>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        }, user_id);
        if (response.getBody() == null) {
            return null;
        } else {
            return response.getBody();
        }
    }

    public Void deleteCoordinates(Long user_id, List<Long> ids, String authToken) {

        String url = "http://localhost:8080/rest/map/v2/coordinates/{user_id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<List<Long>> entity = new HttpEntity<>(ids, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class, user_id);

        return response.getBody();
    }

    public Object addTrips(Long user_id, SavedTripReq savedTripReq, String authToken) {
        String url = "http://localhost:8080/rest/map/v2/trips/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<SavedTripReq> entity = new HttpEntity<>(savedTripReq, headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object getAllUnfinishedTrips(Long userId, String authToken) {
        String url = "http://localhost:8080/rest/map/v2/trips/unfinished/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, userId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object deleteTrips(Long userId, Long tripId, String authToken) {

        String url = "http://localhost:8080/rest/map/v2/trips/delete/{user_id}";

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("trip_id", tripId)
                .buildAndExpand(userId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Object.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }
}
