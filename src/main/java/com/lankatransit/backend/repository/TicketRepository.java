package com.lankatransit.backend.repository;

import com.lankatransit.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket findByQrCodeHash(String qrCodeHash);

    List<Ticket> findByScannedBusIdAndStatus(Long scannedBusId, String status);
    List<Ticket> findByPassengerId(Long passengerId);
}