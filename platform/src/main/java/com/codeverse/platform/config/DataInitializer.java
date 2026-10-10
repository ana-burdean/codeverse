package com.codeverse.platform.config;

import com.codeverse.platform.model.Role;
import com.codeverse.platform.model.User;
import com.codeverse.platform.repository.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds a default ADMIN user if none exists.
 * Username: admin
 * Password: admin123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserJpaRepository userRepo;

    public DataInitializer(UserJpaRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) {
        boolean adminExists = userRepo.findAll().stream().anyMatch(u -> u.getRole() == Role.ADMIN);
        if (!adminExists) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);
            System.out.println("[Init] Created default admin user (admin / admin123)");
        }
    }
}
