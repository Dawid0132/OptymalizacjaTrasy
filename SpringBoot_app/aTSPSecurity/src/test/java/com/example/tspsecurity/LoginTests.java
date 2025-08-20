package com.example.tspsecurity;

import com.example.tspsecurity.DB.Entity.UserRec;
import com.example.tspsecurity.DB.Repository.UserRepositoryRec;
import com.example.tspsecurity.Service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class LoginTests {

    @Mock
    private UserRepositoryRec userRepositoryRec;

    @InjectMocks
    private AuthService authService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        authService = new AuthService(userRepositoryRec, passwordEncoder);
    }

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        String email = "user@gmail.com";
        String rawPassword = "ASdfghjkl123!";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        UserRec userRec = new UserRec(1L, "Jan", "Kowalski", email, encodedPassword);

        Mockito.when(userRepositoryRec.getUserByEmail(email))
                .thenReturn(Mono.just(userRec));

        Mono<UserDetails> result = authService.findByUsername(email);

        StepVerifier.create(result)
                .assertNext(userDetails -> {
                    assertEquals(email, userDetails.getUsername());
                    assertTrue(passwordEncoder.matches(rawPassword, userDetails.getPassword()));
                })
                .verifyComplete();
    }


    @Test
    void shouldReturnErrorWhenUserNotFound() {
        String email = "notfound@example.com";

        Mockito.when(userRepositoryRec.getUserByEmail(email)).thenReturn(Mono.empty());

        Mono<UserDetails> result = authService.findByUsername(email);

        StepVerifier.create(result).expectErrorMatches(throwable -> throwable instanceof UsernameNotFoundException
                && throwable.getMessage().equals("Nie istnieje użytkownik z takim emailem.")).verify();
    }
}
