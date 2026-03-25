package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Booking;
import com.lankatransit.backend.entity.Route;
import com.lankatransit.backend.entity.Halt;
import com.lankatransit.backend.repository.BookingRepository;
import com.lankatransit.backend.repository.RouteRepository;
import com.lankatransit.backend.repository.HaltRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private HaltRepository haltRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // ----- DTOs for Smart Search Response -----
    @Data
    public static class SmartSearchResponse {
        private String startHaltName;
        private String endHaltName;
        private double totalDistanceKm;
        private List<MatchedRoute> availableRoutes;
    }

    @Data
    public static class MatchedRoute {
        private Long routeId;
        private String routeNumber;
        private String fullRouteName;
        private double calculatedFare;
    }
    // ------------------------------------------

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @PostMapping
    public Route createRoute(@RequestBody Route route) {
        return routeRepository.save(route);
    }

    @GetMapping("/{routeId}/halts")
    public List<Halt> getHaltsByRoute(@PathVariable Long routeId) {
        return haltRepository.findByRouteIdOrderBySequenceOrderAsc(routeId);
    }

    @PostMapping("/{routeId}/halts")
    public Halt addHaltToRoute(@PathVariable Long routeId, @RequestBody Halt halt) {
        halt.setRouteId(routeId);
        return haltRepository.save(halt);
    }

    // ========================================================================
    // Smart Node-to-Node Search API
    // ========================================================================
    @GetMapping("/smart-search")
    public ResponseEntity<?> searchSmartRoutes(
            @RequestParam double startLat,
            @RequestParam double startLng,
            @RequestParam double endLat,
            @RequestParam double endLng) {

        List<Route> allRoutes = routeRepository.findAll();
        List<MatchedRoute> matchedRoutes = new ArrayList<>();

        Halt bestStartHalt = null;
        Halt bestEndHalt = null;
        double bestDistance = 0;

        for (Route route : allRoutes) {
            List<Halt> routeHalts = haltRepository.findByRouteIdOrderBySequenceOrderAsc(route.getId());

            Halt nearestStart = findNearestHalt(routeHalts, startLat, startLng);
            Halt nearestEnd = findNearestHalt(routeHalts, endLat, endLng);

            if (nearestStart != null && nearestEnd != null) {
                double distToStartPin = calculateDistance(startLat, startLng, nearestStart.getLatitude().doubleValue(), nearestStart.getLongitude().doubleValue());
                double distToEndPin = calculateDistance(endLat, endLng, nearestEnd.getLatitude().doubleValue(), nearestEnd.getLongitude().doubleValue());

                if (distToStartPin <= 1.5 && distToEndPin <= 1.5) {
                    if (nearestStart.getSequenceOrder() < nearestEnd.getSequenceOrder()) {

                        double travelDistance = nearestEnd.getDistanceFromStart().doubleValue() - nearestStart.getDistanceFromStart().doubleValue();

                        // FIX: Converted BigDecimal baseFarePerKm to double before multiplying
                        double fare = travelDistance * route.getBaseFarePerKm().doubleValue();

                        MatchedRoute matched = new MatchedRoute();
                        matched.setRouteId(route.getId());
                        matched.setRouteNumber(route.getRouteNumber());
                        matched.setFullRouteName(route.getStartLocation() + " -> " + route.getEndLocation());
                        matched.setCalculatedFare(Math.round(fare * 100.0) / 100.0);

                        matchedRoutes.add(matched);

                        if (bestStartHalt == null) {
                            bestStartHalt = nearestStart;
                            bestEndHalt = nearestEnd;
                            bestDistance = travelDistance;
                        }
                    }
                }
            }
        }

        if (matchedRoutes.isEmpty() || bestStartHalt == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "No buses found for this route."));
        }

        SmartSearchResponse response = new SmartSearchResponse();
        response.setStartHaltName(bestStartHalt.getHaltName());
        response.setEndHaltName(bestEndHalt.getHaltName());
        response.setTotalDistanceKm(Math.round(bestDistance * 100.0) / 100.0);
        response.setAvailableRoutes(matchedRoutes);

        return ResponseEntity.ok(response);
    }

    private Halt findNearestHalt(List<Halt> halts, double lat, double lng) {
        Halt nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Halt halt : halts) {
            if (halt.getLatitude() != null && halt.getLongitude() != null) {
                double distance = calculateDistance(lat, lng, halt.getLatitude().doubleValue(), halt.getLongitude().doubleValue());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = halt;
                }
            }
        }
        return nearest;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ========================================================================

    @PostMapping("/book")
    public Booking saveBooking(@RequestBody Booking booking) {
        booking.setBookingTime(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @GetMapping("/bookings/{email}")
    public List<Booking> getUserBookings(@PathVariable String email) {
        return bookingRepository.findByUserEmailOrderByBookingTimeDesc(email);
    }

    @PutMapping("/bookings/{id}/use")
    public ResponseEntity<?> useTicket(@PathVariable Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();

            if ("USED".equals(booking.getStatus())) {
                return ResponseEntity.badRequest().body("Ticket is already used.");
            }

            booking.setStatus("USED");
            bookingRepository.save(booking);
            return ResponseEntity.ok("Ticket successfully used.");
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable Long id, @RequestBody Route updatedRoute) {
        Optional<Route> existingRoute = routeRepository.findById(id);

        if (existingRoute.isPresent()) {
            Route route = existingRoute.get();
            route.setRouteNumber(updatedRoute.getRouteNumber());
            route.setStartLocation(updatedRoute.getStartLocation());
            route.setEndLocation(updatedRoute.getEndLocation());
            route.setBaseFarePerKm(updatedRoute.getBaseFarePerKm());
            routeRepository.save(route);
            return ResponseEntity.ok(java.util.Map.of("message", "Route updated successfully!"));
        }

        return ResponseEntity.notFound().build();
    }

    @org.springframework.transaction.annotation.Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long id) {
        if (routeRepository.existsById(id)) {
            List<Halt> halts = haltRepository.findByRouteIdOrderBySequenceOrderAsc(id);
            if (halts != null && !halts.isEmpty()) {
                haltRepository.deleteAll(halts);
            }

            routeRepository.deleteById(id);
            return ResponseEntity.ok(java.util.Map.of("message", "Route deleted successfully!"));
        }
        return ResponseEntity.notFound().build();
    }
}