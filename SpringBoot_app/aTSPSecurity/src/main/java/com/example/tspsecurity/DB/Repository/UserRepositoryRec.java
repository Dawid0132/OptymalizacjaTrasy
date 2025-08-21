package com.example.tspsecurity.DB.Repository;

import com.example.tspsecurity.DB.Entity.UserRec;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


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
                .map((row, rowMetadata) -> new UserRec(
                        row.get("id", Long.class),
                        row.get("firstname", String.class),
                        row.get("lastname", String.class),
                        row.get("email", String.class),
                        row.get("password", String.class)))
                .one();
    }

}
