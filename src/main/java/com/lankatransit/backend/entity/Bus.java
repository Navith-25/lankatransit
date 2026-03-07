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

    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "bus_number")
    private String busNumber;

    private Integer capacity;
    private String status;
    private String approvalStatus;

    @Column(name = "revenue_license_url")
    private String revenueLicenseUrl;

    @Column(name = "insurance_card_url")
    private String insuranceCardUrl;

    @Column(name = "registration_potha_url")
    private String registrationPothaUrl;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}