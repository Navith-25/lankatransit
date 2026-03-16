package com.lankatransit.backend.repository;

import com.lankatransit.backend.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByStatus(String status);
    Bus findByBusNumber(String busNumber);
    List<Bus> findByOwnerId(Long ownerId);
}