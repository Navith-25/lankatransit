package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Bus;
import com.lankatransit.backend.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    @Autowired
    private BusRepository busRepository;

    @GetMapping
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @PostMapping("/add")
    public Bus addBus(@RequestBody Bus newBus) {
        newBus.setApprovalStatus("PENDING");
        newBus.setStatus("OFFLINE");
        return busRepository.save(newBus);
    }

    @PutMapping("/approve/{id}")
    public Bus approveBus(@PathVariable Long id) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
        bus.setApprovalStatus("APPROVED");
        return busRepository.save(bus);
    }
}