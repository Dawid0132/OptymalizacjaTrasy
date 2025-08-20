package com.example.tsp_rest_api;

import com.example.databaseCore.Entities.Maps.Coordinates;
import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.Maps.Req.Coordinates_Req;
import com.example.databaseCore.Repositories.Maps.CoordinatesRepository;
import com.example.databaseCore.Repositories.User.UserRepository;
import com.example.tsp_rest_api.Service.TspRestApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
public class TspAlgorithmTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TspRestApiService tspRestApiService;

    @Autowired
    private CoordinatesRepository coordinatesRepository;
    private List<Coordinates_Req> coordinates30;
    private List<Coordinates_Req> coordinates10;

    private User testUser;

    @BeforeEach
    void setUpCoordinates() {
        coordinates30 = new ArrayList<>();
        coordinates30.add(new Coordinates_Req(52.2297f, 21.0122f)); // Warszawa
        coordinates30.add(new Coordinates_Req(50.0647f, 19.9450f)); // Kraków
        coordinates30.add(new Coordinates_Req(51.1079f, 17.0385f)); // Wrocław
        coordinates30.add(new Coordinates_Req(53.4285f, 14.5528f)); // Szczecin
        coordinates30.add(new Coordinates_Req(54.3520f, 18.6466f)); // Gdańsk
        coordinates30.add(new Coordinates_Req(51.7592f, 19.4560f)); // Łódź
        coordinates30.add(new Coordinates_Req(50.0413f, 21.9990f)); // Rzeszów
        coordinates30.add(new Coordinates_Req(53.0138f, 18.5984f)); // Bydgoszcz
        coordinates30.add(new Coordinates_Req(51.2465f, 22.5684f)); // Lublin
        coordinates30.add(new Coordinates_Req(51.6681f, 19.6824f)); // Piotrków Trybunalski
        coordinates30.add(new Coordinates_Req(52.4064f, 16.9252f)); // Poznań
        coordinates30.add(new Coordinates_Req(50.8703f, 20.6275f)); // Kielce
        coordinates30.add(new Coordinates_Req(49.9935f, 36.2304f)); // Charków (Ukraina)
        coordinates30.add(new Coordinates_Req(51.9194f, 19.1451f)); // Polska centrum
        coordinates30.add(new Coordinates_Req(54.5189f, 18.5305f)); // Sopot
        coordinates30.add(new Coordinates_Req(53.1325f, 23.1688f)); // Białystok
        coordinates30.add(new Coordinates_Req(49.7461f, 20.2297f)); // Nowy Sącz
        coordinates30.add(new Coordinates_Req(53.7838f, 20.4900f)); // Olsztyn
        coordinates30.add(new Coordinates_Req(51.1167f, 17.0333f)); // Legnica
        coordinates30.add(new Coordinates_Req(51.4027f, 21.1471f)); // Radom
        coordinates30.add(new Coordinates_Req(50.2649f, 19.0238f)); // Katowice
        coordinates30.add(new Coordinates_Req(52.2297f, 21.0122f)); // Warszawa (powtórka)
        coordinates30.add(new Coordinates_Req(53.1325f, 23.1688f)); // Białystok (powtórka)
        coordinates30.add(new Coordinates_Req(50.0413f, 21.9990f)); // Rzeszów (powtórka)
        coordinates30.add(new Coordinates_Req(51.1079f, 17.0385f)); // Wrocław (powtórka)
        coordinates30.add(new Coordinates_Req(53.4285f, 14.5528f)); // Szczecin (powtórka)
        coordinates30.add(new Coordinates_Req(54.3520f, 18.6466f)); // Gdańsk (powtórka)
        coordinates30.add(new Coordinates_Req(51.7592f, 19.4560f)); // Łódź (powtórka)
        coordinates30.add(new Coordinates_Req(50.8703f, 20.6275f)); // Kielce (powtórka)
        coordinates30.add(new Coordinates_Req(49.9935f, 36.2304f)); // Charków (powtórka)


        coordinates10 = new ArrayList<>();
        coordinates10.add(new Coordinates_Req(52.2297f, 21.0122f)); // Warszawa
        coordinates10.add(new Coordinates_Req(50.0647f, 19.9450f)); // Kraków
        coordinates10.add(new Coordinates_Req(51.1079f, 17.0385f)); // Wrocław
        coordinates10.add(new Coordinates_Req(53.4285f, 14.5528f)); // Szczecin
        coordinates10.add(new Coordinates_Req(54.3520f, 18.6466f)); // Gdańsk
        coordinates10.add(new Coordinates_Req(51.7592f, 19.4560f)); // Łódź
        coordinates10.add(new Coordinates_Req(50.0413f, 21.9990f)); // Rzeszów
        coordinates10.add(new Coordinates_Req(53.0138f, 18.5984f)); // Bydgoszcz
        coordinates10.add(new Coordinates_Req(51.2465f, 22.5684f)); // Lublin
        coordinates10.add(new Coordinates_Req(51.6681f, 19.6824f)); // Piotrków Trybunalski
        coordinates10.add(new Coordinates_Req(52.4064f, 16.9252f)); // Poznań

        coordinatesRepository.deleteAll();

        userRepository.deleteAll();

        User user = new User();
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("user_" + UUID.randomUUID() + "@mail.com");
        user.setPassword("Ab!cdeF12");
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setLoggedIn(Boolean.FALSE);
        user.setPasswordChanged(Boolean.FALSE);

        testUser = userRepository.save(user);
    }

    @Test
    void inputDataIdempotence() throws JsonProcessingException {
        for (Coordinates_Req coordinate : coordinates30) {
            Coordinates coordinates = new Coordinates();
            coordinates.setLongitude(coordinate.getLongitude());
            coordinates.setLatitude(coordinate.getLatitude());
            testUser.addCoordinates(coordinates);
        }
        userRepository.save(testUser);
        long count = coordinatesRepository.count();
        Assertions.assertEquals(coordinates30.size(), count);
        ResponseEntity<Object> responseA = tspRestApiService.getRoute(testUser.getId());
        Assertions.assertEquals(HttpStatus.OK, responseA.getStatusCode());
        Assertions.assertNotNull(responseA.getBody());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(responseA.getBody());
        JsonNode root = mapper.readTree(json);
        double distanceA = root.path("trips").get(0).path("distance").asDouble();
        testUser.getCoordinates().clear();
        Collections.reverse(coordinates30);
        for (Coordinates_Req coordinate : coordinates30) {
            Coordinates coordinates = new Coordinates();
            coordinates.setLongitude(coordinate.getLongitude());
            coordinates.setLatitude(coordinate.getLatitude());
            testUser.addCoordinates(coordinates);
        }
        userRepository.save(testUser);
        ResponseEntity<Object> responseB = tspRestApiService.getRoute(testUser.getId());
        Assertions.assertEquals(HttpStatus.OK, responseB.getStatusCode());
        String jsonB = mapper.writeValueAsString(responseB.getBody());
        JsonNode rootB = mapper.readTree(jsonB);
        double distanceB = rootB.path("trips").get(0).path("distance").asDouble();
        Assertions.assertEquals(distanceA, distanceB, 0.01, "The distance route should be the same");
    }

    @Test
    void hamiltonCycle() throws JsonProcessingException {
        for (Coordinates_Req coordinate : coordinates10) {
            Coordinates coordinates = new Coordinates();
            coordinates.setLongitude(coordinate.getLongitude());
            coordinates.setLatitude(coordinate.getLatitude());
            testUser.addCoordinates(coordinates);
        }
        userRepository.save(testUser);
        ResponseEntity<Object> response = tspRestApiService.getRoute(testUser.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response.getBody());
        JsonNode root = mapper.readTree(json);
        JsonNode legs = root.path("trips").get(0).path("legs");
        JsonNode firstLeg = legs.get(0);
        JsonNode firstLegSteps = firstLeg.path("steps");
        JsonNode firstStep = firstLegSteps.get(0);
        JsonNode firstStepIntersections = firstStep.path("intersections");
        JsonNode firstLocation = firstStepIntersections.get(0).path("location");
        double firstLng = firstLocation.get(0).asDouble();
        double firstLat = firstLocation.get(1).asDouble();
        JsonNode lastLeg = legs.get(legs.size() - 1);
        JsonNode lastLegSteps = lastLeg.path("steps");
        JsonNode lastStep = lastLegSteps.get(lastLegSteps.size() - 1);
        JsonNode lastStepIntersections = lastStep.path("intersections");
        JsonNode lastLocation = lastStepIntersections.get(lastStepIntersections.size() - 1).path("location");
        double lastLng = lastLocation.get(0).asDouble();
        double lastLat = lastLocation.get(1).asDouble();
        double tolerance = 0.0001;
        boolean isSamePoint = Math.abs(firstLat - lastLat) < tolerance && Math.abs(firstLng - lastLng) < tolerance;
        Assertions.assertTrue(isSamePoint, "First and last points should be the same (closed cycle)");
    }

//    @Test
//    void measureRouteGenerationTimeFor30Points() {
//        for (Coordinates_Req coordinate : coordinates30) {
//            Coordinates coordinates = new Coordinates();
//            coordinates.setLongitude(coordinate.getLongitude());
//            coordinates.setLatitude(coordinate.getLatitude());
//            testUser.addCoordinates(coordinates);
//        }
//        userRepository.save(testUser);
//
//        long startTime = System.nanoTime();
//        ResponseEntity<Object> response = tspRestApiService.getRoute(testUser.getId());
//        long endTime = System.nanoTime();
//
//        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
//        Assertions.assertNotNull(response.getBody());
//
//        double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
//        System.out.printf("Czas generowania trasy dla 30 punktów: %.3f sekund%n", durationSeconds);
//    }
//
//    @Test
//    void measureRouteGenerationTimeFor10Points() {
//        for (Coordinates_Req coordinate : coordinates10) {
//            Coordinates coordinates = new Coordinates();
//            coordinates.setLongitude(coordinate.getLongitude());
//            coordinates.setLatitude(coordinate.getLatitude());
//            testUser.addCoordinates(coordinates);
//        }
//        userRepository.save(testUser);
//
//        long startTime = System.nanoTime();
//        ResponseEntity<Object> response = tspRestApiService.getRoute(testUser.getId());
//        long endTime = System.nanoTime();
//
//        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
//        Assertions.assertNotNull(response.getBody());
//
//        double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
//        System.out.printf("Czas generowania trasy dla 10 punktów: %.3f sekund%n", durationSeconds);
//    }
}
