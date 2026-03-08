package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.User;
import com.lankatransit.backend.repository.UserRepository;
import com.lankatransit.backend.security.JwtUtil;
import com.lankatransit.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/pending")
    public List<User> getPendingUsers() {
        return userRepository.findByStatus("PENDING");
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User newUser) {
        String encryptedPassword = passwordEncoder.encode(newUser.getPasswordHash());
        newUser.setPasswordHash(encryptedPassword);

        if (newUser.getRole() == null || newUser.getRole().isEmpty()) {
            newUser.setRole("PASSENGER");
            newUser.setStatus("APPROVED");
        } else if (newUser.getRole().equals("OWNER")) {
            newUser.setStatus("PENDING");
        } else {
            newUser.setStatus("APPROVED");
        }

        return userRepository.save(newUser);
    }

    @PostMapping("/add-staff")
    public ResponseEntity<?> addStaff(@RequestBody User staffUser) {
        String tempPassword = "Transit@123";
        staffUser.setPasswordHash(passwordEncoder.encode(tempPassword));

        if ("DRIVER".equals(staffUser.getRole())) {
            staffUser.setStatus("PENDING");
        } else if ("CONDUCTOR".equals(staffUser.getRole())) {
            staffUser.setStatus("APPROVED");
        } else {
            staffUser.setStatus("PENDING");
        }

        User savedUser = userRepository.save(staffUser);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Staff added successfully");
        response.put("id", savedUser.getId());
        response.put("email", savedUser.getEmail());
        response.put("temporaryPassword", tempPassword);
        response.put("status", savedUser.getStatus());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/upload-profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        String fileName = fileStorageService.storeFile(file);
        user.setProfilePhotoUrl("/uploads/" + fileName);
        userRepository.save(user);
        return ResponseEntity.ok("Profile photo uploaded successfully!");
    }

    @PostMapping("/{id}/upload-nic-front")
    public ResponseEntity<?> uploadNicFront(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        String fileName = fileStorageService.storeFile(file);
        user.setNicFrontUrl("/uploads/" + fileName);
        userRepository.save(user);
        return ResponseEntity.ok("NIC Front uploaded successfully!");
    }

    @PostMapping("/{id}/upload-nic-back")
    public ResponseEntity<?> uploadNicBack(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        String fileName = fileStorageService.storeFile(file);
        user.setNicBackUrl("/uploads/" + fileName);
        userRepository.save(user);
        return ResponseEntity.ok("NIC Back uploaded successfully!");
    }

    @PostMapping("/{id}/upload-license")
    public ResponseEntity<?> uploadLicense(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        String fileName = fileStorageService.storeFile(file);
        user.setLicensePhotoUrl("/uploads/" + fileName);
        userRepository.save(user);
        return ResponseEntity.ok("Driving License uploaded successfully!");
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        user.setStatus("APPROVED");
        userRepository.save(user);

        return ResponseEntity.ok(user.getName() + " is successfully APPROVED!");
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

            response.put("status", user.getStatus());
            response.put("id", user.getId());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
}