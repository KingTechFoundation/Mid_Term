package edu.auca.lms.equipment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;

    public EquipmentController(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @GetMapping
    public List<Equipment> list() { return equipmentRepository.findAll(); }

    @GetMapping("/lab/{labId}")
    public List<Equipment> byLab(@PathVariable Long labId) { return equipmentRepository.findByLabId(labId); }

    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    @PostMapping
    public Equipment create(@RequestBody @Valid Equipment equipment) { return equipmentRepository.save(equipment); }

    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Equipment> update(@PathVariable Long id, @RequestBody @Valid Equipment update) {
        return equipmentRepository.findById(id).map(existing -> {
            existing.setLab(update.getLab());
            existing.setCode(update.getCode());
            existing.setName(update.getName());
            existing.setStatus(update.getStatus());
            return ResponseEntity.ok(equipmentRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!equipmentRepository.existsById(id)) return ResponseEntity.notFound().build();
        equipmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

