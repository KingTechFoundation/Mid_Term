package edu.auca.lms.access;

import edu.auca.lms.lab.Lab;
import edu.auca.lms.lab.LabRepository;
import edu.auca.lms.user.User;
import edu.auca.lms.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/access")
public class AccessLogController {
    private final AccessLogRepository accessLogRepository;
    private final LabRepository labRepository;
    private final UserRepository userRepository;

    public AccessLogController(AccessLogRepository accessLogRepository, LabRepository labRepository, UserRepository userRepository) {
        this.accessLogRepository = accessLogRepository;
        this.labRepository = labRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LAB_MANAGER','ADMIN')")
    public List<AccessLog> list() { return accessLogRepository.findAll(); }

    @PostMapping("/enter")
    public ResponseEntity<?> enter(@RequestBody @Valid EnterRequest req, Authentication auth) {
        Lab lab = labRepository.findById(req.getLabId()).orElse(null);
        if (lab == null) return ResponseEntity.badRequest().body("Invalid lab");
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        AccessLog log = AccessLog.builder().lab(lab).user(user).enteredAt(Instant.now()).build();
        accessLogRepository.save(log);
        return ResponseEntity.ok(log);
    }

    @PostMapping("/{id}/exit")
    public ResponseEntity<?> exit(@PathVariable Long id) {
        return accessLogRepository.findById(id).map(l -> {
            l.setExitedAt(Instant.now());
            accessLogRepository.save(l);
            return ResponseEntity.ok(l);
        }).orElse(ResponseEntity.notFound().build());
    }

    public static class EnterRequest {
        @NotNull
        private Long labId;
        public Long getLabId() { return labId; }
        public void setLabId(Long labId) { this.labId = labId; }
    }
}

