package edu.auca.lms.lab;

import edu.auca.lms.common.BaseEntity;
import edu.auca.lms.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "labs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lab extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 128)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 128)
    private String location;

    @NotNull
    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private LabType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;
}

