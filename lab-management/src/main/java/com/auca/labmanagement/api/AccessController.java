package com.auca.labmanagement.api;

import com.auca.labmanagement.api.dto.AccessDtos.AccessResponse;
import com.auca.labmanagement.api.dto.AccessDtos.LogAccessRequest;
import com.auca.labmanagement.domain.AccessLog;
import com.auca.labmanagement.repository.AccessLogRepository;
import com.auca.labmanagement.repository.LabRepository;
import com.auca.labmanagement.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/access")
public class AccessController {
    private final AccessLogRepository accessLogRepository;
    private final LabRepository labRepository;
    private final UserRepository userRepository;

    public AccessController(AccessLogRepository accessLogRepository, LabRepository labRepository, UserRepository userRepository) {
        this.accessLogRepository = accessLogRepository;
        this.labRepository = labRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    public List<AccessResponse> list(@RequestParam Long labId, @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end) {
        return accessLogRepository.findByLabIdAndEnteredAtBetween(labId, start, end).stream().map(this::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER')")
    public ResponseEntity<?> log(@Valid @RequestBody LogAccessRequest req) {
        var lab = labRepository.findById(req.labId()).orElse(null);
        var user = userRepository.findById(req.userId()).orElse(null);
        if (lab == null || user == null) return ResponseEntity.unprocessableEntity().build();
        var entered = req.enteredAt() != null ? req.enteredAt() : OffsetDateTime.now();
        var al = AccessLog.builder().lab(lab).user(user).enteredAt(entered).exitedAt(req.exitedAt()).build();
        al = accessLogRepository.save(al);
        return ResponseEntity.created(URI.create("/api/access/" + al.getId())).body(toResponse(al));
    }

    private AccessResponse toResponse(AccessLog a) {
        return new AccessResponse(a.getId(), a.getLab().getId(), a.getUser().getId(), a.getEnteredAt(), a.getExitedAt());
    }
}

