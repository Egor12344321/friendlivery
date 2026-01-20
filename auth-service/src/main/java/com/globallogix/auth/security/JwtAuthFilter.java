package com.globallogix.auth.security;


import com.globallogix.auth.service.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        final String requestPath = request.getServletPath();
        String path = request.getServletPath();
        String method = request.getMethod();

        log.debug("Filtering request: {} {}", method, path);
        if (requestPath.startsWith("/api/auth/") && requestPath.startsWith("/actuator/") && requestPath.startsWith("/api/actuator/") && requestPath.startsWith("/api/auth/actuator/") && requestPath.startsWith("/actuator/prometheus") &&
                !requestPath.equals("/api/auth/validate")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (authHeader == null || !authHeader.startsWith("Bearer")){
            log.info("No token");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        log.info("JwtAuthFilter: Получен токен: {}", jwt);
        try {

            username = jwtUtil.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                log.info("Аутентификация отсутствует, проверяем токен для пользователя: {}", username);
                if (jwtUtil.validateToken(jwt)){
                    log.info("Token valid {}", username);
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Установлена аутентификация для пользователя: {}", username);
                }
            } else {
                if (username == null) {
                    log.warn("Не удалось извлечь username из токена");
                } else {
                    log.info("Аутентификация уже установлена для пользователя: {}", username);
                }
            }
        } catch (Exception e){
            logger.error("JWT аутентификация неудачная" + e.getMessage());
        }
        filterChain.doFilter(request, response);

    }
}
