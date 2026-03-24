package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Booking;
import com.lankatransit.backend.entity.Route;
import com.lankatransit.backend.entity.Halt;
import com.lankatransit.backend.repository.BookingRepository;
import com.lankatransit.backend.repository.RouteRepository;
import com.lankatransit.backend.repository.HaltRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private HaltRepository haltRepository;

    @Autowired
    private BookingRepository bookingRepository;

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
    public org.springframework.http.ResponseEntity<?> useTicket(@PathVariable Long id) {
        java.util.Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();

            if ("USED".equals(booking.getStatus())) {
                return org.springframework.http.ResponseEntity.badRequest().body("Ticket is already used.");
            }

            booking.setStatus("USED");
            bookingRepository.save(booking);
            return org.springframework.http.ResponseEntity.ok("Ticket successfully used.");
        }

        return org.springframework.http.ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public org.springframework.http.ResponseEntity<?> updateRoute(@PathVariable Long id, @RequestBody Route updatedRoute) {
        java.util.Optional<Route> existingRoute = routeRepository.findById(id);

        if (existingRoute.isPresent()) {
            Route route = existingRoute.get();
            route.setRouteNumber(updatedRoute.getRouteNumber());
            route.setStartLocation(updatedRoute.getStartLocation());
            route.setEndLocation(updatedRoute.getEndLocation());
            route.setBaseFarePerKm(updatedRoute.getBaseFarePerKm());
            routeRepository.save(route);
            return org.springframework.http.ResponseEntity.ok(java.util.Map.of("message", "Route updated successfully!"));
        }

        return org.springframework.http.ResponseEntity.notFound().build();
    }

    @org.springframework.transaction.annotation.Transactional
    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<?> deleteRoute(@PathVariable Long id) {
        if (routeRepository.existsById(id)) {
            List<Halt> halts = haltRepository.findByRouteIdOrderBySequenceOrderAsc(id);
            if (halts != null && !halts.isEmpty()) {
                haltRepository.deleteAll(halts);
            }

            routeRepository.deleteById(id);
            return org.springframework.http.ResponseEntity.ok(java.util.Map.of("message", "Route deleted successfully!"));
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }
}