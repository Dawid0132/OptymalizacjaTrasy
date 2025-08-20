package com.example.tspsecurity;

import com.example.tspsecurity.Semaphors.ConcurrentRequestLimitGlobalFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class ConcurrentRequestTest {

    @InjectMocks
    private ConcurrentRequestLimitGlobalFilter filter;

    @Mock
    private GatewayFilterChain chain;

    private ServerWebExchange exchange;

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);


        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void shouldAllowRequestIfUnderLimit() {
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        Mono<Void> result = filter.filter(exchange, chain);
        verify(chain, times(1)).filter(exchange);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldRejectRequestIfOverLimit() {
        when(chain.filter(any())).thenReturn(Mono.never());
        for (int i = 0; i < 100; i++) {
            filter.filter(
                    MockServerWebExchange.from(MockServerHttpRequest.get("/fake" + i).build()),
                    chain
            ).subscribe();
        }
        GatewayFilterChain normalChain = mock(GatewayFilterChain.class);
        when(normalChain.filter(any())).thenReturn(Mono.empty());
        ServerWebExchange rejectedExchange = MockServerWebExchange.from(MockServerHttpRequest.get("/overflow").build());
        Mono<Void> result = filter.filter(rejectedExchange, normalChain);
        result.block();
        assertThat(Objects.requireNonNull(rejectedExchange.getResponse().getStatusCode()).value()).isEqualTo(429);
    }

}
