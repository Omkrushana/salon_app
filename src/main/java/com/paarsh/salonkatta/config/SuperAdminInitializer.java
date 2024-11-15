package com.paarsh.salonkatta.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.paarsh.salonkatta.model.Role;
import com.paarsh.salonkatta.model.SuperAdmin;
import com.paarsh.salonkatta.model.User;
import com.paarsh.salonkatta.repository.SuperAdminReposiory;
import com.paarsh.salonkatta.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SuperAdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SuperAdminReposiory superAdminReposiory;

    @Bean
    public CommandLineRunner initSuperAdminUser() {
        return args -> {
            // Check if the admin user already exists
            if (!userRepository.existsByEmail("admin@example.com")) {
                // Create a new admin user
                User adminUser = new User();
                adminUser.setEmail("admin@example.com");
                adminUser.setPassword(passwordEncoder.encode("admin_password"));
                adminUser.setRole(Role.SUPERADMIN);
                userRepository.save(adminUser);
            }
            if (superAdminReposiory.findById(1L).isPresent()) {
                log.info("SuperAdmin already present with id 1");
                SuperAdmin superAdmin = superAdminReposiory.findById(1L).get();
                System.out.println(superAdmin.toString());
                superAdminReposiory.save(superAdmin);
            } else {
                SuperAdmin superAdmin = new SuperAdmin();
                superAdminReposiory.save(superAdmin);
            }
        };
    }

}
