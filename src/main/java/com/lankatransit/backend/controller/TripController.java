package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Trip;
import com.lankatransit.backend.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @PostMapping("/start")
    public ResponseEntity<?> startTrip(@RequestBody Trip trip) {
        trip.setStatus("ONGOING");
        trip.setStartTime(new Timestamp(System.currentTimeMillis()));
        Trip savedTrip = tripRepository.save(trip);
        return ResponseEntity.ok(savedTrip);
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<?> endTrip(@PathVariable Long id) {
        Trip trip = tripRepository.findById(id).orElse(null);
        if (trip != null) {
            trip.setStatus("COMPLETED");
            trip.setEndTime(new Timestamp(System.currentTimeMillis()));
            tripRepository.save(trip);
            return ResponseEntity.ok("Trip ended successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/active/driver/{driverId}")
    public ResponseEntity<?> getActiveTripForDriver(@PathVariable Long driverId) {
        List<Trip> activeTrips = tripRepository.findByDriverIdAndStatus(driverId, "ONGOING");
        if (!activeTrips.isEmpty()) {
            return ResponseEntity.ok(activeTrips.get(0));
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @RequestBody Map<String, Double> location) {
        Trip trip = tripRepository.findById(id).orElse(null);
        if (trip != null && "ONGOING".equals(trip.getStatus())) {
            trip.setCurrentLatitude(location.get("latitude"));
            trip.setCurrentLongitude(location.get("longitude"));
            tripRepository.save(trip);
            return ResponseEntity.ok("Location updated");
        }
        return ResponseEntity.badRequest().body("Trip not found or not active");
    }

    @GetMapping("/active/route/{routeId}")
    public ResponseEntity<List<Trip>> getActiveTripsByRoute(@PathVariable Long routeId) {
        List<Trip> activeTrips = tripRepository.findByRouteIdAndStatus(routeId, "ONGOING");
        return ResponseEntity.ok(activeTrips);
    }
}