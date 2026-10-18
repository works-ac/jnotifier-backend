package com.jnotifier;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jnotifier.entity.ERole;
import com.jnotifier.entity.Role;
import com.jnotifier.entity.User;
import com.jnotifier.repository.RoleRepository;
import com.jnotifier.repository.UserRepository;

@SpringBootTest
public class DataInitializerTest {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void testRoleSysadminAndUserSeeded() {
    Optional<Role> sysadminRoleOpt = roleRepository.findByName(ERole.ROLE_SYSADMIN);
    assertThat(sysadminRoleOpt).isPresent();

    Optional<User> userOpt = userRepository.findByUsernameOrEmail("sysadmin@gmail.com", "sysadmin@gmail.com");
    assertThat(userOpt).isPresent();

    User sysadmin = userOpt.get();
    assertThat(sysadmin.getEmail()).isEqualTo("sysadmin@gmail.com");
    assertThat(sysadmin.getFullname()).isEqualTo("System Admin");
    assertThat(sysadmin.getGender()).isEqualTo("M");
    assertThat(sysadmin.getDob()).isNull();
    assertThat(sysadmin.getIsEmailVerified()).isTrue();
    assertThat(sysadmin.getRole().getName()).isEqualTo(ERole.ROLE_SYSADMIN);
    assertThat(passwordEncoder.matches("Sysadmin@12345", sysadmin.getPassword())).isTrue();
  }
}
