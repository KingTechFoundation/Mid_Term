package edu.auca.lms.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface LabBookingRepository extends JpaRepository<LabBooking, Long> {

    @Query("SELECT b FROM LabBooking b WHERE b.lab.id = :labId AND b.status IN (edu.auca.lms.booking.LabBookingStatus.APPROVED, edu.auca.lms.booking.LabBookingStatus.PENDING) AND (b.startTime < :end AND b.endTime > :start)")
    List<LabBooking> findOverlappingBookings(@Param("labId") Long labId,
                                             @Param("start") Instant start,
                                             @Param("end") Instant end);
}

