package com.auca.labmanagement.api;

import com.auca.labmanagement.domain.EquipmentStatus;
import com.auca.labmanagement.repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final MaintenanceRequestRepository maintenanceRepository;
    private final AccessLogRepository accessLogRepository;

    public ReportsController(BookingRepository bookingRepository, EquipmentRepository equipmentRepository, MaintenanceRequestRepository maintenanceRepository, AccessLogRepository accessLogRepository) {
        this.bookingRepository = bookingRepository;
        this.equipmentRepository = equipmentRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.accessLogRepository = accessLogRepository;
    }

    @GetMapping("/lab-usage")
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    public ResponseEntity<?> labUsage(@RequestParam Long labId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {
        var bookings = bookingRepository.findAll().stream().filter(b -> b.getLab().getId().equals(labId) && !b.getEndTime().isBefore(start) && !b.getStartTime().isAfter(end)).toList();
        var accesses = accessLogRepository.findByLabIdAndEnteredAtBetween(labId, start, end);
        return ResponseEntity.ok(Map.of(
                "bookingCount", bookings.size(),
                "accessCount", accesses.size()
        ));
    }

    @GetMapping("/equipment-utilization")
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    public ResponseEntity<?> equipmentUtilization(@RequestParam Long labId) {
        var total = equipmentRepository.findByLabId(labId).size();
        var available = equipmentRepository.findByLabId(labId).stream().filter(e -> e.getStatus() == EquipmentStatus.AVAILABLE).count();
        var inUse = equipmentRepository.findByLabId(labId).stream().filter(e -> e.getStatus() == EquipmentStatus.IN_USE).count();
        var maintenance = equipmentRepository.findByLabId(labId).stream().filter(e -> e.getStatus() == EquipmentStatus.UNDER_MAINTENANCE).count();
        var broken = equipmentRepository.findByLabId(labId).stream().filter(e -> e.getStatus() == EquipmentStatus.BROKEN).count();
        return ResponseEntity.ok(Map.of(
                "total", total,
                "available", available,
                "inUse", inUse,
                "underMaintenance", maintenance,
                "broken", broken
        ));
    }

    @GetMapping("/maintenance-stats")
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    public ResponseEntity<?> maintenanceStats() {
        var all = maintenanceRepository.findAll();
        var costPlaceholder = 0; // Placeholder for future cost integration
        return ResponseEntity.ok(Map.of(
                "count", all.size(),
                "cost", costPlaceholder
        ));
    }
}

