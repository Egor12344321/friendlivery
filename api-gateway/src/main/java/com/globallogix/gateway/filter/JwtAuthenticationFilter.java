package com.globallogix.gateway.filter;

import com.globallogix.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private final Set<String> publicPaths = Set.of(
            "/api/auth/login", "/api/auth/register",
            "/api/auth/refresh", "/auth/refresh",
            "/auth/login", "/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("GATEWAY GLOBAL FILTER");
        log.info("Path: {}", path);

        if (isPublicPath(path)) {
            log.info("Public path, skipping JWT check");
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Auth header present: {}", authHeader != null);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No JWT token provided");
            return unauthorized(exchange, "Missing JWT token");
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtUtil.validateToken(token);

        if (!isValid) {
            log.warn("Invalid token");
            return unauthorized(exchange, "Invalid token");
        }
        log.info("Получение userId");
        String userId = String.valueOf(jwtUtil.extractUserId(token));

        log.info("Valid token ID: {}", userId);

        var modRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userId)
                .build();

        log.info("Headers added, forwarding to service");
        return chain.filter(exchange.mutate().request(modRequest).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        log.error("Unauthorized: {}", message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}