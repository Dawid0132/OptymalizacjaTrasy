package com.example.atspsecurity.JWT;

import io.jsonwebtoken.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private long accessTokenValidity = 60 * 60 * 1000;
    private final JwtParser jwtParser;
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil(JwtProperties jwtProperties) {
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().isEmpty())
            throw new IllegalArgumentException();
        this.jwtProperties = jwtProperties;
        this.jwtParser = Jwts.parser().setSigningKey(jwtProperties.getSecret());
    }

    public String createToken(String email, String firstname, String lastname) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("firstname", firstname);
        claims.put("lastname", lastname);
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + Duration.ofMinutes(accessTokenValidity).toMillis());
        return Jwts.builder().setClaims(claims).setExpiration(tokenValidity).signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret()).compact();
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(ServerHttpRequest request) {
        try {
            String token = resolveToken(request);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException e) {
            request.getAttributes().put("expired", e.getMessage());
            throw e;
        } catch (Exception e) {
            request.getAttributes().put("invalid", e.getMessage());
            throw e;
        }
    }

    public String resolveToken(ServerHttpRequest request) {
        List<String> bearerToken = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
        if (!bearerToken.isEmpty() && bearerToken.get(0).startsWith(TOKEN_PREFIX)) {
            return bearerToken.get(0).substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    private List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }
}
