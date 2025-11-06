package com.globallogix.gateway.filter;


import com.globallogix.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    private final Set<String> publicPaths = Set.of(
            "/api/auth/login", "/api/auth/register",
            "/api/auth/refresh", "/auth/refresh",
            "/auth/login", "/auth/register"
    );
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            System.out.println("Checking path: " + path);
            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                System.out.println("No jwt token provided");
                return unauthorized(exchange, "Missing jwt token");
            }

            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);

            if (!isValid){
                System.out.println("Invalid token");
                return unauthorized(exchange, "invalid token");
            }

            String userId = jwtUtil.extractAllClaims(token).getSubject();

            System.out.println("Valid token, user: " + userId);
            var modRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .build();
            return chain.filter(exchange.mutate().request(modRequest).build());
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        System.out.println("Returning 401: " + message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public boolean isPublicPath(String path){
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    public static class Config{

    }
}
