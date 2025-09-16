package com.auca.labmanagement.repository;

import com.auca.labmanagement.domain.Lab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabRepository extends JpaRepository<Lab, Long> {
    Optional<Lab> findByName(String name);
}

