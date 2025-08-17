package com.example.tsp_service.TspService;

import com.example.tsp_service.Pojos.Coordinates;
import com.example.tsp_service.Pojos.SavedTrips.SavedTripReq;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class AppService {
    private final RestTemplate restTemplate;

    public AppService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object generateRoute(Long user_id) {
        String url = "http://localhost:8080/rest/map/v2/getRoute/{user_id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object getTrip(Long user_id, Long trip_id) {
        String url = "http://localhost:8080/rest/map/v2/getRoute/trip/{user_id}";

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("trip_id", trip_id)
                .buildAndExpand(user_id)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Object> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Object.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object getLegs(Long userId, UUID mapName) {
        String url = "http://localhost:8080/rest/map/v2/getRoute/legs/{user_id}";

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("map_name", mapName)
                .buildAndExpand(userId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Object> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Object.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Coordinates verifyLastClickedCoordinates(Long user_id, Coordinates coordinates) {
        String url = "http://localhost:8080/rest/map/v2/coordinatesVerify/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Coordinates> entity = new HttpEntity<>(coordinates, headers);

        ResponseEntity<Coordinates> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Coordinates.class, user_id);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public List<Coordinates> addCoordinates(Long user_id, Coordinates coordinates) {
        String url = "http://localhost:8080/rest/map/v2/coordinatesVerify/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Coordinates> entity = new HttpEntity<>(coordinates, headers);
        ParameterizedTypeReference<List<Coordinates>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<Coordinates>> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public List<Coordinates> getListOfCoordinates(Long user_id) {
        String url = "http://localhost:8080/rest/map/v2/coordinates/{user_id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Coordinates>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        }, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public List<Coordinates> deleteCoordinates(Long user_id, List<Long> ids) {

        String url = "http://localhost:8080/rest/map/v2/coordinates/{user_id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<List<Long>> entity = new HttpEntity<>(ids, headers);
        ParameterizedTypeReference<List<Coordinates>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<Coordinates>> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object addTrips(Long user_id, SavedTripReq savedTripReq) {
        String url = "http://localhost:8080/rest/map/v2/trips/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<SavedTripReq> entity = new HttpEntity<>(savedTripReq, headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class, user_id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object getAllUnfinishedTrips(Long userId) {
        String url = "http://localhost:8080/rest/map/v2/trips/unfinished/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, userId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object deleteTrips(Long userId, Long tripId) {

        String url = "http://localhost:8080/rest/map/v2/trips/delete/{user_id}";

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("trip_id", tripId)
                .buildAndExpand(userId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Object.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Boolean startDriving(Long userId, UUID mapName) {
        String url = "http://localhost:8080/rest/map/v2/trips/startDriving/{user_id}";

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("map_name", mapName)
                .buildAndExpand(userId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, Boolean.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Boolean measuringTimeStatus(Long userId, UUID mapName) {
        String url = "http://localhost:8080/rest/map/v2/trips/measuringTime/status/{user_id}";

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("map_name", mapName)
                .buildAndExpand(userId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Boolean.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object finishTrip(Long userId, Long trip_id) {

        String url = "http://localhost:8080/rest/map/v2/trips/finish/{user_id}";

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("trip_id", trip_id)
                .buildAndExpand(userId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public Object getAllFinishedTrips(Long userId) {
        String url = "http://localhost:8080/rest/map/v2/trips/finished/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, userId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }
}
