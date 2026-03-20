package com.lankatransit.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(name = "passenger_id")
    private Long passengerId;

    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "start_halt_id")
    private Long startHaltId;

    @Column(name = "end_halt_id")
    private Long endHaltId;

    @Column(name = "travel_date")
    private LocalDate travelDate;

    private BigDecimal fare;

    @Column(name = "qr_code_hash")
    private String qrCodeHash;

    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "scanned_bus_id")
    private Long scannedBusId;
}