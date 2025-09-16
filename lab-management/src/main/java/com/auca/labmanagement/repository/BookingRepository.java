package com.auca.labmanagement.repository;

import com.auca.labmanagement.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.lab.id = :labId and b.endTime > :start and b.startTime < :end")
    List<Booking> findOverlapping(@Param("labId") Long labId,
                                  @Param("start") OffsetDateTime start,
                                  @Param("end") OffsetDateTime end);
}

