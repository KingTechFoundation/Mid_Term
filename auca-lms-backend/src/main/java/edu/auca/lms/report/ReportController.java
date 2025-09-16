package edu.auca.lms.report;

import edu.auca.lms.access.AccessLogRepository;
import edu.auca.lms.booking.LabBookingRepository;
import edu.auca.lms.equipment.EquipmentRepository;
import edu.auca.lms.maintenance.MaintenanceRequestRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('LAB_MANAGER','ADMIN')")
public class ReportController {
    private final LabBookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final MaintenanceRequestRepository maintenanceRepository;
    private final AccessLogRepository accessLogRepository;

    public ReportController(LabBookingRepository bookingRepository, EquipmentRepository equipmentRepository, MaintenanceRequestRepository maintenanceRepository, AccessLogRepository accessLogRepository) {
        this.bookingRepository = bookingRepository;
        this.equipmentRepository = equipmentRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.accessLogRepository = accessLogRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary() {
        return ResponseEntity.ok(Map.of(
                "bookings", bookingRepository.count(),
                "equipment", equipmentRepository.count(),
                "maintenance", maintenanceRepository.count(),
                "accessLogs", accessLogRepository.count()
        ));
    }
}

