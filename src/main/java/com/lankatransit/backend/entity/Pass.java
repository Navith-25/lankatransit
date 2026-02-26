package com.lankatransit.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "passes")
@Data
public class Pass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pass_id")
    private Long id;

    @Column(name = "passenger_id")
    private Long passengerId;

    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "pass_type")
    private String passType;

    @Column(name = "valid_from")
    private Timestamp validFrom;

    @Column(name = "valid_until")
    private Timestamp validUntil;

    private BigDecimal price;

    @Column(name = "qr_code_hash")
    private String qrCodeHash;

    private String status;
}