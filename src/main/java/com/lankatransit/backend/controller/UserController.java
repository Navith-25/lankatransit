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
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "This email is already registered!"));
        }

        try {
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

            User savedUser = userRepository.save(newUser);

            return ResponseEntity.ok(Map.of(
                    "message", "Registration successful",
                    "id", savedUser.getId(),
                    "email", savedUser.getEmail()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed due to server error"));
        }
    }

    @PostMapping("/add-staff")
    public ResponseEntity<?> addStaff(@RequestBody User staffUser) {

        if (userRepository.findByEmail(staffUser.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "This email is already registered in the system!"));
        }

        try {
            if (!"DRIVER".equals(staffUser.getRole()) && !"CONDUCTOR".equals(staffUser.getRole())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid role for staff."));
            }

            String encryptedPassword = passwordEncoder.encode(staffUser.getPasswordHash());
            staffUser.setPasswordHash(encryptedPassword);

            if ("DRIVER".equals(staffUser.getRole()) || "CONDUCTOR".equals(staffUser.getRole())) {
                staffUser.setStatus("CREATED");
            } else {
                staffUser.setStatus("APPROVED");
            }

            User savedUser = userRepository.save(staffUser);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Staff account created successfully");
            response.put("id", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("status", savedUser.getStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Database error occurred"));
        }
    }

    @PutMapping("/submit-docs/{id}")
    public ResponseEntity<?> submitDocuments(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        user.setStatus("PENDING");
        userRepository.save(user);

        return ResponseEntity.ok("Documents submitted successfully. Status updated to PENDING.");
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

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setStatus("REJECTED");
        userRepository.save(user);
        return ResponseEntity.ok(user.getName() + " is REJECTED!");
    }

    @PutMapping("/resubmit/{id}")
    public ResponseEntity<?> resubmitUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setStatus("RESUBMIT");
        userRepository.save(user);
        return ResponseEntity.ok(user.getName() + " needs to resubmit documents!");
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<User>> getStaffByOwner(@PathVariable Long ownerId) {
        List<User> staff = userRepository.findByOwnerId(ownerId);
        return ResponseEntity.ok(staff);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Staff member deleted successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable Long id, @RequestBody User updatedUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setRole(updatedUser.getRole());

        userRepository.save(existingUser);
        return ResponseEntity.ok("Staff updated successfully");
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