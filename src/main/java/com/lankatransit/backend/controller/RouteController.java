package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Route;
import com.lankatransit.backend.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteRepository routeRepository;

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
}