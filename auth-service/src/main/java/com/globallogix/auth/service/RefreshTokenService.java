package com.globallogix.auth.service;


import com.globallogix.auth.entity.RefreshTokenEntity;
import com.globallogix.auth.entity.User;
import com.globallogix.auth.repository.RefreshTokenRepository;
import com.globallogix.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenEntity createRefreshToken(User user, String token){
        refreshTokenRepository.revokeAllUserTokens(user);
        return RefreshTokenEntity.builder()
                .revoked(false)
                .token(token)
                .user(user)
                .build();
    }

    public void revokeRefreshToken(User user){
        refreshTokenRepository.revokeAllUserTokens(user);
    }


}
