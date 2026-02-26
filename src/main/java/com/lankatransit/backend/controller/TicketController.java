package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Ticket;
import com.lankatransit.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}