package edu.auca.lms.equipment;

import edu.auca.lms.common.BaseEntity;
import edu.auca.lms.lab.Lab;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lab_id", nullable = false)
    private Lab lab;

    @NotBlank
    @Column(nullable = false, length = 64)
    private String code;

    @NotBlank
    @Column(nullable = false, length = 128)
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, length = 32)
    private EquipmentStatus status;
}

