package com.lankatransit.backend.repository;

import com.lankatransit.backend.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
}