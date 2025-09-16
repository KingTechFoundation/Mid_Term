package com.auca.labmanagement.api;

import com.auca.labmanagement.api.dto.EquipmentDtos.CreateEquipmentRequest;
import com.auca.labmanagement.api.dto.EquipmentDtos.EquipmentResponse;
import com.auca.labmanagement.api.dto.EquipmentDtos.UpdateEquipmentRequest;
import com.auca.labmanagement.domain.Equipment;
import com.auca.labmanagement.domain.EquipmentStatus;
import com.auca.labmanagement.repository.EquipmentRepository;
import com.auca.labmanagement.repository.LabRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    private final EquipmentRepository equipmentRepository;
    private final LabRepository labRepository;

    public EquipmentController(EquipmentRepository equipmentRepository, LabRepository labRepository) {
        this.equipmentRepository = equipmentRepository;
        this.labRepository = labRepository;
    }

    @GetMapping
    public List<EquipmentResponse> list(@RequestParam(required = false) Long labId) {
        var list = labId == null ? equipmentRepository.findAll() : equipmentRepository.findByLabId(labId);
        return list.stream().map(this::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LAB_MANAGER')")
    public ResponseEntity<EquipmentResponse> create(@Valid @RequestBody CreateEquipmentRequest req) {
        var lab = labRepository.findById(req.labId()).orElse(null);
        if (lab == null) return ResponseEntity.unprocessableEntity().build();
        var eq = Equipment.builder()
                .assetTag(req.assetTag())
                .model(req.model())
                .status(EquipmentStatus.AVAILABLE)
                .lab(lab)
                .build();
        eq = equipmentRepository.save(eq);
        return ResponseEntity.created(URI.create("/api/equipment/" + eq.getId())).body(toResponse(eq));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> get(@PathVariable Long id) {
        return equipmentRepository.findById(id).map(e -> ResponseEntity.ok(toResponse(e))).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LAB_MANAGER')")
    public ResponseEntity<EquipmentResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateEquipmentRequest req) {
        var opt = equipmentRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        var eq = opt.get();
        if (req.model() != null) eq.setModel(req.model());
        if (req.status() != null) eq.setStatus(req.status());
        eq = equipmentRepository.save(eq);
        return ResponseEntity.ok(toResponse(eq));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LAB_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!equipmentRepository.existsById(id)) return ResponseEntity.notFound().build();
        equipmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private EquipmentResponse toResponse(Equipment e) {
        return new EquipmentResponse(e.getId(), e.getAssetTag(), e.getModel(), e.getStatus(), e.getLab().getId());
    }
}

