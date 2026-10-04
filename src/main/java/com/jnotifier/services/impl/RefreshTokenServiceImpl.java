package com.jnotifier.services.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.jnotifier.exception.RefreshTokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jnotifier.exception.TokenRefreshException;
import com.jnotifier.entity.RefreshToken;
import com.jnotifier.repository.RefreshTokenRepository;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.services.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${jnotifier.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        // Remove any existing token for this user
        refreshTokenRepository.deleteByUser(refreshToken.getUser());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException(token.getToken(), "Refresh token was expired. Please make a new sign in request");
        }

        return token;
    }

    @Override
    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }
}
