package com.lankatransit.backend;

import com.lankatransit.backend.entity.User;
import com.lankatransit.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        User existingAdmin = userRepository.findByEmail("admin@lankatransit.com");

        if (existingAdmin == null) {
            System.out.println("No Admin found. Creating default Admin account...");

            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@lankatransit.com");

            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));

            admin.setRole("ADMIN");
            admin.setStatus("ACTIVE");
            admin.setGamificationPoints(0);

            userRepository.save(admin);

            System.out.println("Admin account created successfully!");
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}