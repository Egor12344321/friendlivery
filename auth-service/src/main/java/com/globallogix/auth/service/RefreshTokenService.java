package com.globallogix.auth.service;


import com.globallogix.auth.dto.response.AuthResponse;
import com.globallogix.auth.dto.response.UpdateTokens;
import com.globallogix.auth.entity.User;
import com.globallogix.auth.exception.TokenNotFoundException;
import com.globallogix.auth.exception.InvalidTokenRefreshException;
import com.globallogix.auth.exception.UserNotFoundException;
import com.globallogix.auth.repository.UserRepository;
import com.globallogix.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshRedisService refreshRedisService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public ResponseCookie setRefreshTokenToCookie(AuthResponse response){
        ResponseCookie refreshCookie = ResponseCookie.from("refresh", response.getRefreshToken())
                .httpOnly(true)
                .maxAge(7 * 24 * 60 * 60)
                .build();
        // удаляем рефреш из респонса, чтобы он не пошел в бади в запросе, помещаем его в хедер куки
        response.setRefreshToken(null);
        return  refreshCookie;
    }

    public UpdateTokens updateTokens(String refresh) {
            log.info("SERVICE: Updating tokens");
            String username = jwtUtil.extractUsername(refresh);
            if (!jwtUtil.validateToken(refresh)){
                throw new InvalidTokenRefreshException("Срок действия рефршен токена истек, надо заново регистрироваться");
            }
            String refreshFromRedis = refreshRedisService.getRefreshTokenFromCache(username)
                    .orElseThrow(() -> new TokenNotFoundException("Токен не найден"));
            log.info("Токен из кук найден и является валидным для пользователя: {}", username);
            log.info("Токен из кук: {}", refresh);
            log.info("Токен из cache найден и является валидным для пользователя: {}", jwtUtil.extractUsername(refreshFromRedis));
            log.info("Токен из Redis: {}", refreshFromRedis);

            if (jwtUtil.validateToken(refresh) && refresh.equals(refreshFromRedis)) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с таким username не найден"));

                String updatedRefresh = jwtUtil.generateRefreshToken(user);
                refreshRedisService.saveRefreshToCache(updatedRefresh, username);

                return UpdateTokens.builder()
                        .accessToken(jwtUtil.generateToken(user))
                        .refreshToken(updatedRefresh)
                        .message("Токены обновлены")
                        .build();
            }
            log.error("Рефреш в редисе и в куках не совпадает");
            throw new InvalidTokenRefreshException("Токены в куках и редисе не совпадают");


    }


}
