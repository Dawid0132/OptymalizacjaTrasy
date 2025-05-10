package com.example.serviceuserauthrest.Service;

import com.example.serviceuserauthrest.Pojo.Request.UserRegister;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceUserAuthRestService {
    private final RestTemplate restTemplate;

    public ServiceUserAuthRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String register(UserRegister userRegister) {
        String url = "http://localhost:8888/rest/user/v2/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserRegister> entity = new HttpEntity<>(userRegister, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }
}
