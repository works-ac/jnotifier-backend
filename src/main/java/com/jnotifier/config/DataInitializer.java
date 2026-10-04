package com.jnotifier.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jnotifier.entity.ERole;
import com.jnotifier.entity.Role;
import com.jnotifier.entity.User;
import com.jnotifier.repository.RoleRepository;
import com.jnotifier.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jnotifier.system.INIT_USER:sysadmin@gmail.com}")
    private String initUser;

    @Value("${jnotifier.system.INIT_PASS:Sysadmin@12345}")
    private String initPass;

    @Value("${jnotifier.system.INIT_NAME:System Admin}")
    private String initName;

    @Value("${jnotifier.system.INIT_SEX:M}")
    private String initSex;

    @Override
    public void run(String... args) throws Exception {
        for (ERole erole : ERole.values()) {
            if (roleRepository.findByName(erole).isEmpty()) {
                roleRepository.save(new Role(erole));
            }
        }

        logger.info("All roles have been initialized successfully");

        // Registering system admin user if not exists.
        if (userRepository.existsByEmail(initUser)) {
            logger.info("System admin user already exists in db with email {}, skipping creation of system admin.", initUser);
            return;
        }

        Role sysAdminRole = roleRepository.findByName(ERole.ROLE_SYSADMIN)
                .orElseThrow(() -> new RuntimeException("Error: ROLE_SYSADMIN not found in database"));

        String username = initUser.contains("@") ? initUser.substring(0, initUser.indexOf('@')) : initUser;
        username += "_" + System.currentTimeMillis();

        User sysAdmin = new User();
        sysAdmin.setUsername(username);
        sysAdmin.setFullname(initName);
        sysAdmin.setEmail(initUser);
        sysAdmin.setPassword(passwordEncoder.encode(initPass));
        sysAdmin.setGender(initSex);
        sysAdmin.setDob(null);
        sysAdmin.setRole(sysAdminRole);
        sysAdmin.setIsEmailVerified(true);
        sysAdmin.setIsSuspended(false);
        sysAdmin.setIsDeleted(false);
        sysAdmin.setIsPwd(false);

        userRepository.save(sysAdmin);
        logger.info("SYSADMIN registered successfully with email: {}", initUser);
    }
}

