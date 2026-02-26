package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Halt;
import com.lankatransit.backend.repository.HaltRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/halts")
public class HaltController {

    @Autowired
    private HaltRepository haltRepository;

    @GetMapping
    public List<Halt> getAllHalts() {
        return haltRepository.findAll();
    }
}