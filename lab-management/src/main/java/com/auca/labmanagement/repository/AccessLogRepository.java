package com.auca.labmanagement.repository;

import com.auca.labmanagement.domain.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findByLabIdAndEnteredAtBetween(Long labId, OffsetDateTime start, OffsetDateTime end);
}

