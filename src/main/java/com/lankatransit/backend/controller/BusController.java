package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Bus;
import com.lankatransit.backend.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}