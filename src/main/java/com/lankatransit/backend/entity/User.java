package com.lankatransit.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private String role;
    private String status;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "nic_front_url")
    private String nicFrontUrl;

    @Column(name = "nic_back_url")
    private String nicBackUrl;

    @Column(name = "license_photo_url")
    private String licensePhotoUrl;

    @Column(name = "gamification_points")
    private Integer gamificationPoints;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}