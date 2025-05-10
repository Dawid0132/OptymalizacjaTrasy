package com.example.serviceuserauthrest.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;

@RestControllerAdvice
public class serviceUserConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
