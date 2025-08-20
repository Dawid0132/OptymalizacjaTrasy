package com.example.databaseCore.Repositories.Maps;

import com.example.databaseCore.Entities.Maps.MeasuringTime;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface MeasuringTimeRepository extends CrudRepository<MeasuringTime, Long> {
}
