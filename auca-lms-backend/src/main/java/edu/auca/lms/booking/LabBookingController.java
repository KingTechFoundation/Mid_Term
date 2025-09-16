package edu.auca.lms.booking;

import edu.auca.lms.lab.Lab;
import edu.auca.lms.lab.LabRepository;
import edu.auca.lms.user.Role;
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
@RequestMapping("/api/bookings")
public class LabBookingController {

    private final LabBookingRepository bookingRepository;
    private final LabRepository labRepository;
    private final UserRepository userRepository;

    public LabBookingController(LabBookingRepository bookingRepository, LabRepository labRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.labRepository = labRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<LabBooking> list() { return bookingRepository.findAll(); }

    @PreAuthorize("hasAnyRole('INSTRUCTOR','STUDENT')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateBookingRequest req, Authentication auth) {
        Lab lab = labRepository.findById(req.getLabId()).orElse(null);
        if (lab == null) return ResponseEntity.badRequest().body("Invalid lab");
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<LabBooking> overlapping = bookingRepository.findOverlappingBookings(lab.getId(), req.getStartTime(), req.getEndTime());
        if (!overlapping.isEmpty()) {
            return ResponseEntity.badRequest().body("Time slot overlaps with existing booking");
        }
        LabBooking booking = LabBooking.builder()
                .lab(lab)
                .requestedBy(user)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(LabBookingStatus.PENDING)
                .build();
        bookingRepository.save(booking);
        return ResponseEntity.ok(booking);
    }

    @PreAuthorize("hasAnyRole('LAB_MANAGER','ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        return bookingRepository.findById(id).map(b -> {
            b.setStatus(LabBookingStatus.APPROVED);
            bookingRepository.save(b);
            return ResponseEntity.ok(b);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('LAB_MANAGER','ADMIN')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return bookingRepository.findById(id).map(b -> {
            b.setStatus(LabBookingStatus.REJECTED);
            bookingRepository.save(b);
            return ResponseEntity.ok(b);
        }).orElse(ResponseEntity.notFound().build());
    }

    public static class CreateBookingRequest {
        @NotNull
        private Long labId;
        @NotNull
        private Instant startTime;
        @NotNull
        private Instant endTime;

        public Long getLabId() { return labId; }
        public void setLabId(Long labId) { this.labId = labId; }
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
    }
}

