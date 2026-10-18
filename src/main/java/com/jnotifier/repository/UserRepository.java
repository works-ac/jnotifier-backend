package com.jnotifier.repository;

import java.util.Optional;

import com.jnotifier.helpers.query.UserQueries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jnotifier.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Optional<User> findByUsernameOrEmail(String username, String email);

  Boolean existsByUsername(String username);
  Boolean existsByEmail(String email);

  @Query(UserQueries.GET_ALL_USER_DETAILS)
  Page<User> findAllUserDetailsExceptSA(Pageable pageable);
}
