package com.example.serviceuserauthrest.Service;

import com.example.serviceuserauthrest.Pojo.Request.PasswordChanged;
import com.example.serviceuserauthrest.Pojo.Request.UserRegister;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ServiceUserAuthRestService {
    private final RestTemplate restTemplate;

    public ServiceUserAuthRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String register(UserRegister userRegister) {
        String url = "http://localhost:8090/rest/user/v2/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserRegister> entity = new HttpEntity<>(userRegister, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public Void logout(Long user_id, String authToken) {
        String url = "http://localhost:8090/rest/user/v2/logout/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class, user_id);

        return response.getBody();
    }

    public Void password_verify(Long userId, PasswordChanged passwordChanged, String authToken) {
        String url = "http://localhost:8090/rest/user/v2/password/verify/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<PasswordChanged> entity = new HttpEntity<>(passwordChanged, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class, userId);

        return response.getBody();
    }

    public Void password_change(Long userId, PasswordChanged passwordChanged, String authToken) {
        String url = "http://localhost:8090/rest/user/v2/password/change/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<PasswordChanged> entity = new HttpEntity<>(passwordChanged, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class, userId);

        return response.getBody();
    }

    public Void account_delete(Long userId, String authToken) {
        String url = "http://localhost:8090/rest/user/v2/account/delete/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class, userId);

        return response.getBody();
    }
}
