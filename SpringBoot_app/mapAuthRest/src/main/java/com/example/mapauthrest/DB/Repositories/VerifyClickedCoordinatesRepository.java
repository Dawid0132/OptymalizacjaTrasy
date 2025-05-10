package com.example.mapauthrest.DB.Repositories;

import com.example.mapauthrest.DB.Entities.VerifyClickedCoordinates;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerifyClickedCoordinatesRepository extends CrudRepository<VerifyClickedCoordinates, Long> {
    Optional<VerifyClickedCoordinates> findByUserId(Long userId);
}
