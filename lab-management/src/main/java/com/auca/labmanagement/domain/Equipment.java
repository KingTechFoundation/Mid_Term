package com.auca.labmanagement.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String assetTag;

    @Column(nullable = false, length = 200)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EquipmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", nullable = false)
    private Lab lab;

    private OffsetDateTime lastMaintenanceAt;
}

