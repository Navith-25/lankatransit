package com.lankatransit.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "buses")
@Data
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_id")
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "bus_number")
    private String busNumber;

    private Integer capacity;
    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}