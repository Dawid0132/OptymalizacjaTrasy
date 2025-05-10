package com.example.mapauthrest.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MapRestApiService {

    private final RestTemplate restTemplate;

    public MapRestApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


}
