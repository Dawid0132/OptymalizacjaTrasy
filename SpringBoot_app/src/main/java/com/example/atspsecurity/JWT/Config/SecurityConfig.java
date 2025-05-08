package com.example.atspsecurity.JWT.Config;

import com.example.atspsecurity.JWT.Components.CustomAccessDeniedHandler;
import com.example.atspsecurity.JWT.Components.CustomAuthenticationEntryPoint;
import com.example.atspsecurity.JWT.Components.JwtAuthenticationFilter;
import com.example.atspsecurity.Service.AuthService;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final AuthService authService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomAccessDeniedHandler customAccessDeniedHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, AuthService authService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.authService = authService;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes().route("userAuthRest", route -> route.path("/rest/user/v2/**")
                        .filters(gatewayFilterSpec -> {
                            gatewayFilterSpec.addResponseHeader("res-header", "res-header-value");
                            return gatewayFilterSpec;
                        }).uri("http://localhost:8090"))
                .route("userAuthRestService", route -> route.path("/rest/user/v1/**")
                        .filters(gatewayFilterSpec -> {
                            gatewayFilterSpec.addResponseHeader("res-header", "res-header-value");
                            return gatewayFilterSpec;
                        }).uri("http://localhost:8091"))
                .route("mapAuthRest", route -> route.path("/rest/map/v2/**")
                        .filters(gatewayFilterSpec -> {
                            gatewayFilterSpec.addResponseHeader("res-header", "res-header-value");
                            return gatewayFilterSpec;
                        }).uri("http://localhost:8080"))
                .route("mapAuthRestService", route -> route.path("/rest/map/v1/**")
                        .filters(gatewayFilterSpec -> {
                            gatewayFilterSpec.addResponseHeader("res-header", "res-header-value");
                            return gatewayFilterSpec;
                        }).uri("http://localhost:8081")).build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/auth/**",
                                "/rest/user/v2/register",
                                "/rest/user/v1/register").permitAll()
                        .anyExchange().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return httpSecurity.build();
    }


    @Bean
    public ReactiveAuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(authService);
        manager.setPasswordEncoder(passwordEncoder);
        return manager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
