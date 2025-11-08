package com.globallogix.gateway.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;


    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims extractAllClaims(String token) {
        log.info("JwtUtil: Получение всех claims");
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Токен истек");
        } catch (JwtException e) {
            throw new RuntimeException("Невалидный токен");
        }
    }
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Object extractUserId(String token) {

        Claims claims = extractAllClaims(token);
        Object userIdObj = claims.get("userId");
        log.info("UserId from token - type: {}, value: {}",
                userIdObj != null ? userIdObj.getClass().getSimpleName() : "null",
                userIdObj);

        return userIdObj;
    }
    public Boolean validateToken(String token){
        try{
            extractAllClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
