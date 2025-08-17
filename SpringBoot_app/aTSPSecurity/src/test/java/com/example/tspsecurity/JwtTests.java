package com.example.tspsecurity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldBeNotAuthenticated() {
        long userId = 1L;
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/rest/user/v1/" + userId + "/user/get",
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRegisterLoginAndAccessProtectedEndpoint() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String email = "example12@gmail.com";
        String password = "ABCqweasd123!";
        String confirmPassword = "ABCqweasd123!";
        Map<String, String> registerPayload = new HashMap<>();
        registerPayload.put("_firstname", firstName);
        registerPayload.put("_lastname", lastName);
        registerPayload.put("_email", email);
        registerPayload.put("_password", password);
        registerPayload.put("_confirm_password", confirmPassword);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> registerRequest = new HttpEntity<>(registerPayload, headers);
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/rest/user/v1/register",
                registerRequest,
                String.class
        );
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", email);
        loginPayload.put("password", password);
        HttpEntity<Map<String, String>> loginRequest = new HttpEntity<>(loginPayload, headers);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login",
                loginRequest,
                String.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        JsonNode json = objectMapper.readTree(loginResponse.getBody());
        Long id = json.get("id").asLong();
        String token = json.get("token").asText();
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        HttpEntity<Void> authRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<String> secureResponse = restTemplate.exchange(
                "http://localhost:" + port + "/rest/user/v1/" + id + "/user/get",
                HttpMethod.GET,
                authRequest,
                String.class
        );
        assertThat(secureResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldRejectAccessWhenUserIdMismatch() throws Exception {
        String email = "example12@gmail.com";
        String password = "ABCqweasd123!";
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", email);
        loginPayload.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> loginRequest = new HttpEntity<>(loginPayload, headers);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login",
                loginRequest,
                String.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode json = objectMapper.readTree(loginResponse.getBody());
        Long actualUserId = json.get("id").asLong();
        String token = json.get("token").asText();
        Long fakeUserId = actualUserId + 1;
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        HttpEntity<Void> authRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/rest/user/v1/" + fakeUserId + "/user/get",
                HttpMethod.GET,
                authRequest,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

}
