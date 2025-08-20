package com.example.tsp_rest_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.databaseCore.Repositories")
@EntityScan(basePackages = "com.example.databaseCore.Entities")
public class TspRestUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(TspRestUserApplication.class, args);
    }

}
