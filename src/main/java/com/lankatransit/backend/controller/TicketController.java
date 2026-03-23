package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Bus;
import com.lankatransit.backend.entity.Ticket;
import com.lankatransit.backend.repository.BookingRepository;
import com.lankatransit.backend.repository.BusRepository;
import com.lankatransit.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @GetMapping("/revenue/owner/{ownerId}")
    public ResponseEntity<Map<String, Object>> getOwnerRevenue(@PathVariable Long ownerId) {
        Map<String, Object> response = new HashMap<>();

        List<Bus> ownerBuses = busRepository.findByOwnerId(ownerId);

        double totalRevenue = 0.0;
        long totalTickets = 0L;

        Set<Long> routeIds = ownerBuses.stream()
                .map(Bus::getRouteId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        for (Long routeId : routeIds) {
            Long ticketsCount = bookingRepository.countUsedTicketsByRoute(routeId);
            Double revenue = bookingRepository.calculateTotalRevenueByRoute(routeId);

            if (ticketsCount != null) {
                totalTickets += ticketsCount;
            }
            if (revenue != null) {
                totalRevenue += revenue;
            }
        }

        response.put("totalBuses", ownerBuses.size());
        response.put("totalTickets", totalTickets);
        response.put("totalRevenue", totalRevenue);

        return ResponseEntity.ok(response);
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