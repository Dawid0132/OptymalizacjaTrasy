package com.example.mapauthrest.DB.Repositories;

import com.example.mapauthrest.DB.Entities.Coordinates;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CoordinatesRepository extends CrudRepository<Coordinates, Long> {
    List<Coordinates> findAllByUserId(Long userId);

    Optional<Coordinates> findById(Long id);

    @Modifying
    void deleteCoordinatesByIds(List<Long> ids);
}
