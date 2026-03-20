package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Bus;
import com.lankatransit.backend.entity.Ticket;
import com.lankatransit.backend.repository.BusRepository;
import com.lankatransit.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BusRepository busRepository;

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @GetMapping("/revenue/owner/{ownerId}")
    public ResponseEntity<?> getOwnerRevenue(@PathVariable Long ownerId) {
        List<Bus> ownerBuses = busRepository.findByOwnerId(ownerId);
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalScanned = 0;

        for (Bus bus : ownerBuses) {
            List<Ticket> tickets = ticketRepository.findByScannedBusIdAndStatus(bus.getId(), "SCANNED");
            for (Ticket t : tickets) {
                if (t.getFare() != null) {
                    totalRevenue = totalRevenue.add(t.getFare());
                    totalScanned++;
                }
            }
        }

        return ResponseEntity.ok(Map.of(
                "totalRevenue", totalRevenue,
                "totalTickets", totalScanned,
                "totalBuses", ownerBuses.size()
        ));
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scanTicket(@RequestBody Map<String, String> payload) {
        String scannedHash = payload.get("qrCodeHash");
        String busIdStr = payload.get("busId");

        if (scannedHash == null || scannedHash.isEmpty() || busIdStr == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "QR Code or Bus ID missing!"));
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
        ticket.setScannedBusId(Long.parseLong(busIdStr));
        ticketRepository.save(ticket);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Ticket Verified! Valid Passenger.",
                "fare", ticket.getFare()
        ));
    }
}