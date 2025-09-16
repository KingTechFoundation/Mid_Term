package edu.auca.lms.booking;

import edu.auca.lms.common.BaseEntity;
import edu.auca.lms.lab.Lab;
import edu.auca.lms.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "lab_bookings", indexes = {
        @Index(name = "idx_lab_booking_lab_start_end", columnList = "lab_id,start_time,end_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabBooking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lab_id", nullable = false)
    private Lab lab;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @NotNull
    @Future
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Future
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LabBookingStatus status;
}

