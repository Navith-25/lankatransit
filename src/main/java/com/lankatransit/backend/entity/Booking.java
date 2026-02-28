package com.lankatransit.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long routeId;
    private String startHalt;
    private String endHalt;
    private Double fare;
    private LocalDateTime bookingTime;
    private String userEmail;
}