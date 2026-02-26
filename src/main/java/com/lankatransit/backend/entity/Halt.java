package com.lankatransit.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "halts")
@Data
public class Halt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "halt_id")
    private Long id;

    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "halt_name")
    private String haltName;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "distance_from_start")
    private BigDecimal distanceFromStart;
}