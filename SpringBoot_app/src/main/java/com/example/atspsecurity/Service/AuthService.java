package com.example.atspsecurity.Service;

import com.example.atspsecurity.DB.Repository.UserRepositoryRec;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService implements ReactiveUserDetailsService {
    private final UserRepositoryRec userRepositoryRec;

    public AuthService(UserRepositoryRec userRepositoryRec) {
        this.userRepositoryRec = userRepositoryRec;
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepositoryRec.getUserByEmail(email)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("Nie istnieje użytkownik z takim emailem."))))
                .map(_user -> {
                    List<String> roles = new ArrayList<>();
                    roles.add("USER");
                    return User.builder()
                            .username(_user.email())
                            .password(_user.password())
                            .roles(roles.toArray(new String[0]))
                            .build();
                });
    }
}
