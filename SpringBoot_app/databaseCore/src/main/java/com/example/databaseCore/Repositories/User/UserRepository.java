package com.example.databaseCore.Repositories.User;


import com.example.databaseCore.Entities.User.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
