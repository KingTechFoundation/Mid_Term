package com.auca.labmanagement.repository;

import com.auca.labmanagement.domain.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByLabId(Long labId);
}

