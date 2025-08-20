package com.example.databaseCore.Repositories.Maps;


import com.example.databaseCore.Entities.Maps.MeasuringTime;
import com.example.databaseCore.Entities.Maps.Trips;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TripsRepository extends CrudRepository<Trips, Long> {
    Optional<Trips> findByNameAndUserId(UUID map_name, Long user_id);

    Optional<Trips> findByIdAndUserId(Long trip_id, Long user_id);

    Optional<List<Trips>> getAllByFinishedCondition(Long user_id, Boolean condition);
}
