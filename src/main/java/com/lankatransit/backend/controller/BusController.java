package com.lankatransit.backend.controller;

import com.lankatransit.backend.entity.Bus;
import com.lankatransit.backend.repository.BusRepository;
import com.lankatransit.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @GetMapping("/pending")
    public List<Bus> getPendingBuses() {
        return busRepository.findByStatus("PENDING");
    }

    @PostMapping
    public ResponseEntity<?> addBus(@RequestBody Bus bus) {
        try {
            if (busRepository.findByBusNumber(bus.getBusNumber()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bus number already exists!");
            }

            bus.setStatus("PENDING");
            bus.setApprovalStatus("PENDING");
            Bus savedBus = busRepository.save(bus);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding bus");
        }
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approveBus(@PathVariable Long id) {
        Bus bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bus not found");
        }
        bus.setStatus("APPROVED");
        bus.setApprovalStatus("APPROVED");
        busRepository.save(bus);
        return ResponseEntity.ok(bus.getBusNumber() + " is successfully APPROVED!");
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> rejectBus(@PathVariable Long id) {
        Bus bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bus not found");
        }
        bus.setStatus("REJECTED");
        bus.setApprovalStatus("REJECTED");
        busRepository.save(bus);
        return ResponseEntity.ok(bus.getBusNumber() + " is REJECTED!");
    }

    @PutMapping("/resubmit/{id}")
    public ResponseEntity<String> resubmitBus(@PathVariable Long id) {
        Bus bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bus not found");
        }
        bus.setStatus("RESUBMIT");
        bus.setApprovalStatus("RESUBMIT");
        busRepository.save(bus);
        return ResponseEntity.ok(bus.getBusNumber() + " needs to resubmit documents!");
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Bus>> getBusesByOwner(@PathVariable Long ownerId) {
        List<Bus> buses = busRepository.findByOwnerId(ownerId);
        return ResponseEntity.ok(buses);
    }


    @PostMapping("/{id}/upload-revenue-license")
    public ResponseEntity<?> uploadRevenueLicense(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
        String fileName = fileStorageService.storeFile(file);
        bus.setRevenueLicenseUrl("/uploads/" + fileName);
        busRepository.save(bus);
        return ResponseEntity.ok("Revenue License uploaded successfully!");
    }

    @PostMapping("/{id}/upload-insurance")
    public ResponseEntity<?> uploadInsurance(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
        String fileName = fileStorageService.storeFile(file);
        bus.setInsuranceCardUrl("/uploads/" + fileName);
        busRepository.save(bus);
        return ResponseEntity.ok("Insurance Card uploaded successfully!");
    }

    @PostMapping("/{id}/upload-registration")
    public ResponseEntity<?> uploadRegistration(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
        String fileName = fileStorageService.storeFile(file);
        bus.setRegistrationPothaUrl("/uploads/" + fileName);
        busRepository.save(bus);
        return ResponseEntity.ok("Registration Potha uploaded successfully!");
    }

    @PostMapping("/{id}/upload-route-permit")
    public ResponseEntity<?> uploadRoutePermit(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
        String fileName = fileStorageService.storeFile(file);
        bus.setRoutePermitUrl("/uploads/" + fileName);
        busRepository.save(bus);
        return ResponseEntity.ok("Route Permit uploaded successfully!");
    }
}