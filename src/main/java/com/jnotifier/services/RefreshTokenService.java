package com.jnotifier.services;

import java.util.Optional;
import com.jnotifier.entity.RefreshToken;
import com.jnotifier.helpers.query.RefreshTokenQueries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenService {
  Optional<RefreshToken> findByToken(String token);
  Optional<RefreshToken> findByUserId(Long userId);
  RefreshToken createRefreshToken(Long userId);
  RefreshToken verifyExpiration(RefreshToken token);
  int deleteByUserId(Long userId);
}
