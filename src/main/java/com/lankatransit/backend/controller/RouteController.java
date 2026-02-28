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
}