package com.example.tsp_rest_user_service.Service;

import com.example.tsp_rest_user_service.Pojo.Request.PasswordChanged;
import com.example.tsp_rest_user_service.Pojo.Request.UserRegister;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserService {
    private final RestTemplate restTemplate;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String register(UserRegister userRegister) {
        String url = "http://user-auth-rest:8090/rest/user/v2/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserRegister> entity = new HttpEntity<>(userRegister, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public Object getUser(Long user_id) {
        String url = "http://user-auth-rest:8090/rest/user/v2/user/get/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, user_id);

        return response.getBody();
    }

    public Void logout(Long user_id) {
        String url = "http://user-auth-rest:8090/rest/user/v2/logout/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class, user_id);

        return response.getBody();
    }

    public Void password_verify(Long userId, PasswordChanged passwordChanged) {
        String url = "http://user-auth-rest:8090/rest/user/v2/password/verify/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        HttpEntity<PasswordChanged> entity = new HttpEntity<>(passwordChanged, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class, userId);

        return response.getBody();
    }

    public Void password_change(Long userId, PasswordChanged passwordChanged) {
        String url = "http://user-auth-rest:8090/rest/user/v2/password/change/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        HttpEntity<PasswordChanged> entity = new HttpEntity<>(passwordChanged, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class, userId);

        return response.getBody();
    }

    public Void account_delete(Long userId) {
        String url = "http://user-auth-rest:8090/rest/user/v2/account/delete/{user_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class, userId);

        return response.getBody();
    }
}
