package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Pass;
import com.lankatransit.backend.repository.PassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/passes")
public class PassController {

    @Autowired
    private PassRepository passRepository;

    @GetMapping
    public List<Pass> getAllPasses() {
        return passRepository.findAll();
    }
}