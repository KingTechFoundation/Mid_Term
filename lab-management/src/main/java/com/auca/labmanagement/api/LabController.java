package com.auca.labmanagement.api;

import com.auca.labmanagement.api.dto.LabDtos.CreateLabRequest;
import com.auca.labmanagement.api.dto.LabDtos.LabResponse;
import com.auca.labmanagement.api.dto.LabDtos.UpdateLabRequest;
import com.auca.labmanagement.domain.Lab;
import com.auca.labmanagement.repository.LabRepository;
import com.auca.labmanagement.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/labs")
public class LabController {
    private final LabRepository labRepository;
    private final UserRepository userRepository;

    public LabController(LabRepository labRepository, UserRepository userRepository) {
        this.labRepository = labRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<LabResponse> list() {
        return labRepository.findAll().stream().map(this::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LAB_MANAGER')")
    public ResponseEntity<LabResponse> create(@Valid @RequestBody CreateLabRequest req) {
        var lab = Lab.builder()
                .name(req.name())
                .location(req.location())
                .capacity(req.capacity())
                .type(req.type())
                .assignedManager(req.managerId() == null ? null : userRepository.findById(req.managerId()).orElse(null))
                .build();
        lab = labRepository.save(lab);
        return ResponseEntity.created(URI.create("/api/labs/" + lab.getId())).body(toResponse(lab));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabResponse> get(@PathVariable Long id) {
        return labRepository.findById(id).map(l -> ResponseEntity.ok(toResponse(l))).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LAB_MANAGER')")
    public ResponseEntity<LabResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateLabRequest req) {
        var labOpt = labRepository.findById(id);
        if (labOpt.isEmpty()) return ResponseEntity.notFound().build();
        var lab = labOpt.get();
        if (req.location() != null) lab.setLocation(req.location());
        if (req.capacity() != null) lab.setCapacity(req.capacity());
        if (req.type() != null) lab.setType(req.type());
        if (req.managerId() != null) {
            var manager = userRepository.findById(req.managerId()).orElse(null);
            lab.setAssignedManager(manager);
        }
        lab = labRepository.save(lab);
        return ResponseEntity.ok(toResponse(lab));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!labRepository.existsById(id)) return ResponseEntity.notFound().build();
        labRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private LabResponse toResponse(Lab l) {
        return new LabResponse(l.getId(), l.getName(), l.getLocation(), l.getCapacity(), l.getType(), l.getAssignedManager() == null ? null : l.getAssignedManager().getId());
    }
}

