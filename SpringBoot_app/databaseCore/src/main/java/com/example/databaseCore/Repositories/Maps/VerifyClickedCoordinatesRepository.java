package com.example.databaseCore.Repositories.Maps;


import com.example.databaseCore.Entities.Maps.VerifyClickedCoordinates;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerifyClickedCoordinatesRepository extends CrudRepository<VerifyClickedCoordinates, Long> {
    Optional<VerifyClickedCoordinates> findByUserId(Long userId);
}
