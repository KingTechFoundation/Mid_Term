package edu.auca.lms.maintenance;

import edu.auca.lms.equipment.Equipment;
import edu.auca.lms.equipment.EquipmentRepository;
import edu.auca.lms.user.Role;
import edu.auca.lms.user.User;
import edu.auca.lms.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public List<MaintenanceRequest> list() { return maintenanceRepository.findAll(); }

    @PreAuthorize("hasAnyRole('LAB_MANAGER','INSTRUCTOR','STUDENT')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateMaintenanceRequest req, Authentication auth) {
        Equipment equipment = equipmentRepository.findById(req.getEquipmentId()).orElse(null);
        if (equipment == null) return ResponseEntity.badRequest().body("Invalid equipment");
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        MaintenanceRequest mr = MaintenanceRequest.builder()
                .equipment(equipment)
                .requestedBy(user)
                .issueDescription(req.getIssueDescription())
                .status(MaintenanceStatus.OPEN)
                .build();
        maintenanceRepository.save(mr);
        return ResponseEntity.ok(mr);
    }

    @PreAuthorize("hasAnyRole('LAB_MANAGER','ADMIN')")
    @PostMapping("/{id}/assign/{userId}")
    public ResponseEntity<?> assign(@PathVariable Long id, @PathVariable Long userId) {
        return maintenanceRepository.findById(id).map(mr -> {
            User technician = userRepository.findById(userId).orElse(null);
            if (technician == null) return ResponseEntity.badRequest().body("Invalid technician");
            mr.setAssignedTo(technician);
            mr.setStatus(MaintenanceStatus.IN_PROGRESS);
            maintenanceRepository.save(mr);
            return ResponseEntity.ok(mr);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('LAB_MANAGER','ADMIN','TECHNICIAN')")
    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable Long id) {
        return maintenanceRepository.findById(id).map(mr -> {
            mr.setStatus(MaintenanceStatus.RESOLVED);
            maintenanceRepository.save(mr);
            return ResponseEntity.ok(mr);
        }).orElse(ResponseEntity.notFound().build());
    }

    public static class CreateMaintenanceRequest {
        @NotNull
        private Long equipmentId;
        @NotBlank
        private String issueDescription;

        public Long getEquipmentId() { return equipmentId; }
        public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
        public String getIssueDescription() { return issueDescription; }
        public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
    }
}

