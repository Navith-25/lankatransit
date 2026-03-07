package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.User;
import com.lankatransit.backend.repository.UserRepository;
import com.lankatransit.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User newUser) {
        String encryptedPassword = passwordEncoder.encode(newUser.getPasswordHash());
        newUser.setPasswordHash(encryptedPassword);

        if (newUser.getRole() == null || newUser.getRole().isEmpty()) {
            newUser.setRole("PASSENGER");
        }

        return userRepository.save(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody com.lankatransit.backend.dto.LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {


            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole());
            response.put("email", user.getEmail());
            response.put("name", user.getName());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
}