package com.example.tspsecurity.JWT.Components;


import com.example.tspsecurity.JWT.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String accessToken = jwtUtil.resolveToken(request);

        System.out.println("[JWT FILTER] Path: " + path);
        System.out.println("[JWT FILTER] Access Token: " + (accessToken != null ? "Present" : "Missing"));

        if (accessToken == null) {
            System.out.println("[JWT FILTER] No token found — allowing through without authentication.");
            return chain.filter(exchange);
        }

        try {
            Claims claims = jwtUtil.resolveClaims(request);
            System.out.println("[JWT FILTER] Claims: " + (claims != null ? claims.toString() : "null"));
            if (claims != null && jwtUtil.validateClaims(claims)) {
                String email = claims.getSubject();
                Long userIdFromToken = jwtUtil.getUserIdFromToken(accessToken);

                System.out.println("[JWT FILTER] Email from token: " + email);
                System.out.println("[JWT FILTER] User ID from token: " + userIdFromToken);

                Pattern pattern = Pattern.compile("^/rest/(user|map)/v1/(\\d+)/.*");
                Matcher matcher = pattern.matcher(path);

                if (matcher.matches()) {
                    String userIdFromPath = matcher.group(2);

                    System.out.println("[JWT FILTER] User ID from path: " + userIdFromPath);

                    if (!userIdFromPath.equals(String.valueOf(userIdFromToken))) {

                        System.out.println("[JWT FILTER] User ID mismatch! Rejecting request.");

                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>());
                SecurityContext context = new SecurityContextImpl(auth);

                System.out.println("[JWT FILTER] Authentication successful. Proceeding with SecurityContext.");

                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
            } else {
                System.out.println("[JWT FILTER] Invalid or missing claims.");
            }
        } catch (Exception e) {
            System.err.println("JWT authentication error: " + e.getMessage());
        }
        return chain.filter(exchange);
    }
}
