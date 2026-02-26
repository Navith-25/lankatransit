package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.User;
import com.lankatransit.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @PostMapping("/register")
    public User registerUser(@RequestBody User newUser) {
        String encryptedPassword = passwordEncoder.encode(newUser.getPasswordHash());
        newUser.setPasswordHash(encryptedPassword);

        return userRepository.save(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody com.lankatransit.backend.dto.LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
}