package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Ticket;
import com.lankatransit.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scanTicket(@RequestBody Map<String, String> payload) {
        String scannedHash = payload.get("qrCodeHash");

        if (scannedHash == null || scannedHash.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "QR Code data missing!"));
        }

        Ticket ticket = ticketRepository.findByQrCodeHash(scannedHash);

        if (ticket == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid Ticket! Fake QR Code."));
        }

        if ("SCANNED".equals(ticket.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Ticket Already Used! Cannot scan again."));
        }

        if (!"VALID".equals(ticket.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Ticket is Expired or Cancelled!"));
        }

        ticket.setStatus("SCANNED");
        ticketRepository.save(ticket);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Ticket Verified! Valid Passenger.",
                "fare", ticket.getFare()
        ));
    }
}