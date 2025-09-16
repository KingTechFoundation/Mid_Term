package com.auca.labmanagement.api;

import com.auca.labmanagement.api.dto.MaintenanceDtos.CreateMaintenanceRequest;
import com.auca.labmanagement.api.dto.MaintenanceDtos.MaintenanceResponse;
import com.auca.labmanagement.api.dto.MaintenanceDtos.UpdateMaintenanceRequest;
import com.auca.labmanagement.domain.MaintenanceRequest;
import com.auca.labmanagement.domain.MaintenanceStatus;
import com.auca.labmanagement.repository.EquipmentRepository;
import com.auca.labmanagement.repository.MaintenanceRequestRepository;
import com.auca.labmanagement.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {
    private final MaintenanceRequestRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public MaintenanceController(MaintenanceRequestRepository maintenanceRepository, EquipmentRepository equipmentRepository, UserRepository userRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<MaintenanceResponse> list(@RequestParam(required = false) Long equipmentId) {
        var list = equipmentId == null ? maintenanceRepository.findAll() : maintenanceRepository.findByEquipmentId(equipmentId);
        return list.stream().map(this::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER','INSTRUCTOR')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateMaintenanceRequest req, Authentication auth) {
        var eq = equipmentRepository.findById(req.equipmentId()).orElse(null);
        if (eq == null) return ResponseEntity.unprocessableEntity().body("Equipment not found");
        var user = userRepository.findByUsername(auth.getName()).orElse(null);
        var mr = MaintenanceRequest.builder()
                .equipment(eq)
                .requestedBy(user)
                .status(MaintenanceStatus.OPEN)
                .createdAt(OffsetDateTime.now())
                .description(req.description())
                .build();
        mr = maintenanceRepository.save(mr);
        return ResponseEntity.created(URI.create("/api/maintenance/" + mr.getId())).body(toResponse(mr));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateMaintenanceRequest req) {
        var opt = maintenanceRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        var m = opt.get();
        if (req.status() != null) {
            m.setStatus(req.status());
            if (req.status() == MaintenanceStatus.RESOLVED || req.status() == MaintenanceStatus.REPLACED) {
                m.setResolvedAt(OffsetDateTime.now());
            }
        }
        if (req.assignedToId() != null) {
            var tech = userRepository.findById(req.assignedToId()).orElse(null);
            m.setAssignedTo(tech);
        }
        m = maintenanceRepository.save(m);
        return ResponseEntity.ok(toResponse(m));
    }

    private MaintenanceResponse toResponse(MaintenanceRequest m) {
        return new MaintenanceResponse(m.getId(), m.getEquipment().getId(), m.getRequestedBy() == null ? null : m.getRequestedBy().getId(), m.getAssignedTo() == null ? null : m.getAssignedTo().getId(), m.getStatus(), m.getDescription());
    }
}

