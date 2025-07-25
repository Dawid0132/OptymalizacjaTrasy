package com.example.databaseCore.Repositories.Maps;


import com.example.databaseCore.Entities.Maps.Coordinates;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CoordinatesRepository extends CrudRepository<Coordinates, Long> {
    List<Coordinates> findByUserId(Long id);
}
