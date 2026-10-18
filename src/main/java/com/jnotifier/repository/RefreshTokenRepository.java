package com.jnotifier.repository;

import java.util.Optional;

import com.jnotifier.helpers.query.RefreshTokenQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jnotifier.entity.RefreshToken;
import com.jnotifier.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Query(RefreshTokenQueries.GET_USER_BY_REFRESH_TOKEN)
  Optional<RefreshToken> findByUserId(@Param("userId") Long userId);

  @Modifying
  int deleteByUser(User user);
}
