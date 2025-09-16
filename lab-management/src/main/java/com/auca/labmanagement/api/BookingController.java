package com.auca.labmanagement.api;

import com.auca.labmanagement.api.dto.BookingDtos.BookingResponse;
import com.auca.labmanagement.api.dto.BookingDtos.CreateBookingRequest;
import com.auca.labmanagement.api.dto.BookingDtos.UpdateBookingRequest;
import com.auca.labmanagement.domain.Booking;
import com.auca.labmanagement.repository.BookingRepository;
import com.auca.labmanagement.repository.LabRepository;
import com.auca.labmanagement.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingRepository bookingRepository;
    private final LabRepository labRepository;
    private final UserRepository userRepository;

    public BookingController(BookingRepository bookingRepository, LabRepository labRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.labRepository = labRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<BookingResponse> list(@RequestParam(required = false) Long labId) {
        var list = labId == null ? bookingRepository.findAll() : bookingRepository.findAll().stream().filter(b -> b.getLab().getId().equals(labId)).toList();
        return list.stream().map(this::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER','INSTRUCTOR','STUDENT')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateBookingRequest req, Authentication auth) {
        var lab = labRepository.findById(req.labId()).orElse(null);
        if (lab == null) return ResponseEntity.unprocessableEntity().body("Lab not found");
        if (!req.endTime().isAfter(req.startTime())) return ResponseEntity.unprocessableEntity().body("End must be after start");
        var overlapping = bookingRepository.findOverlapping(lab.getId(), req.startTime(), req.endTime());
        if (!overlapping.isEmpty()) return ResponseEntity.status(409).body("Time slot already booked");
        var user = userRepository.findByUsername(auth.getName()).orElse(null);
        var booking = Booking.builder()
                .lab(lab)
                .createdBy(user)
                .type(req.type())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .purpose(req.purpose())
                .build();
        booking = bookingRepository.save(booking);
        return ResponseEntity.created(URI.create("/api/bookings/" + booking.getId())).body(toResponse(booking));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> get(@PathVariable Long id) {
        return bookingRepository.findById(id).map(b -> ResponseEntity.ok(toResponse(b))).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER','INSTRUCTOR','STUDENT')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateBookingRequest req) {
        var opt = bookingRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        var b = opt.get();
        final Long bookingId = b.getId();
        var newStart = req.startTime() != null ? req.startTime() : b.getStartTime();
        var newEnd = req.endTime() != null ? req.endTime() : b.getEndTime();
        if (!newEnd.isAfter(newStart)) return ResponseEntity.unprocessableEntity().body("End must be after start");
        var overlapping = bookingRepository.findOverlapping(b.getLab().getId(), newStart, newEnd).stream().filter(x -> !x.getId().equals(bookingId)).toList();
        if (!overlapping.isEmpty()) return ResponseEntity.status(409).body("Time slot already booked");
        if (req.startTime() != null) b.setStartTime(req.startTime());
        if (req.endTime() != null) b.setEndTime(req.endTime());
        if (req.purpose() != null) b.setPurpose(req.purpose());
        bookingRepository.save(b);
        return ResponseEntity.ok(toResponse(b));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LAB_MANAGER','INSTRUCTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!bookingRepository.existsById(id)) return ResponseEntity.notFound().build();
        bookingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(b.getId(), b.getLab().getId(), b.getCreatedBy() == null ? null : b.getCreatedBy().getId(), b.getType(), b.getStartTime(), b.getEndTime(), b.getPurpose());
    }
}

