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

    @Column(name = "password_hash")
    private String passwordHash;

    private String role;

    @Column(name = "gamification_points")
    private Integer gamificationPoints;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}