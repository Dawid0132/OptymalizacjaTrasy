package com.example.atspsecurity.Controller;

import com.example.atspsecurity.DB.Repository.UserRepositoryRec;
import com.example.atspsecurity.JWT.JwtUtil;
import com.example.atspsecurity.Pojo.Request.LoginReq;
import com.example.atspsecurity.Pojo.Response.LoginRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ReactiveAuthenticationManager authenticationManager;

    private final UserRepositoryRec userRepositoryRec;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public AuthController(ReactiveAuthenticationManager authenticationManager, UserRepositoryRec userRepositoryRec, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepositoryRec = userRepositoryRec;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginRes>> login(@RequestBody LoginReq loginReq) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword());
        return authenticationManager.authenticate(authToken)
                .flatMap(authentication -> userRepositoryRec.getUserByEmail(authentication.getName()))
                .map(user -> {
                    String token = jwtUtil.createToken(user.email(), user.firstname(), user.lastname());
                    LoginRes loginRes = new LoginRes(user.id(), user.firstname(), user.lastname(), user.email(), token);
                    return ResponseEntity.ok(loginRes);
                })
                .switchIfEmpty(Mono.fromRunnable(() -> System.out.println("Authentication failed: user not found"))
                        .then(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())))
                .onErrorResume(e -> {
                    System.out.println("Login error: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }
}
