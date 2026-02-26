package com.lankatransit.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "routes")
@Data
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long id;

    @Column(name = "route_number")
    private String routeNumber;

    @Column(name = "start_location")
    private String startLocation;

    @Column(name = "end_location")
    private String endLocation;

    @Column(name = "base_fare_per_km")
    private BigDecimal baseFarePerKm;
}