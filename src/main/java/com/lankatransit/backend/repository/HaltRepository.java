package com.lankatransit.backend.repository;

import com.lankatransit.backend.entity.Halt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HaltRepository extends JpaRepository<Halt, Long> {
    List<Halt> findByRouteIdOrderBySequenceOrderAsc(Long routeId);
}