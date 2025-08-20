package com.example.tspsecurity.Service;


import com.example.tspsecurity.DB.Repository.UserRepositoryRec;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService implements ReactiveUserDetailsService {

    private final UserRepositoryRec userRepositoryRec;

    private final PasswordEncoder passwordEncoder;


    public AuthService(UserRepositoryRec userRepositoryRec, PasswordEncoder passwordEncoder) {
        this.userRepositoryRec = userRepositoryRec;
        this.passwordEncoder = passwordEncoder;
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
