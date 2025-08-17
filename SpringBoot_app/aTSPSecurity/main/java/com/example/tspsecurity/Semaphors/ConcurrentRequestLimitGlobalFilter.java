package com.example.tspsecurity.Semaphors;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.Semaphore;

@Component
public class ConcurrentRequestLimitGlobalFilter implements GlobalFilter, Ordered {

    private final Semaphore semaphore = new Semaphore(100);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!semaphore.tryAcquire()) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange)
                .doFinally(_ -> semaphore.release());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
