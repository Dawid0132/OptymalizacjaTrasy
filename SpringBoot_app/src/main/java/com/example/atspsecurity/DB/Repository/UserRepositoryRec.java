package com.example.atspsecurity.DB.Repository;

import com.example.atspsecurity.DB.Entity.UserRec;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryRec {
    private final DatabaseClient databaseClient;

    public UserRepositoryRec(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<UserRec> getUserByEmail(String email) {
        return databaseClient
                .sql("select * from user u where u.email=:email")
                .bind("email", email)
                .map((row, metadata) -> new UserRec(
                        row.get("id", Long.class),
                        row.get("firstname", String.class),
                        row.get("lastname", String.class),
                        row.get("email", String.class),
                        row.get("password", String.class)))
                .one();
    }
}
