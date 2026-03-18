package com.lankatransit.backend.repository;

import com.lankatransit.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByDriverIdAndStatus(Long driverId, String status);

    List<Trip> findByRouteIdAndStatus(Long routeId, String status);
}