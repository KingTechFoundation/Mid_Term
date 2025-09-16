package edu.auca.lms.lab;

import edu.auca.lms.user.Role;
import edu.auca.lms.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labs")
public class LabController {

    private final LabRepository labRepository;

    public LabController(LabRepository labRepository) {
        this.labRepository = labRepository;
    }

    @GetMapping
    public List<Lab> list() { return labRepository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Lab> get(@PathVariable Long id) {
        return labRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    @PostMapping
    public Lab create(@RequestBody @Valid Lab lab) { return labRepository.save(lab); }

    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Lab> update(@PathVariable Long id, @RequestBody @Valid Lab update) {
        return labRepository.findById(id).map(existing -> {
            existing.setName(update.getName());
            existing.setLocation(update.getLocation());
            existing.setCapacity(update.getCapacity());
            existing.setType(update.getType());
            existing.setManager(update.getManager());
            return ResponseEntity.ok(labRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!labRepository.existsById(id)) return ResponseEntity.notFound().build();
        labRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

