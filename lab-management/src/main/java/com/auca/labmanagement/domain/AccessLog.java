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
@Table(name = "access_logs", indexes = {
        @Index(name = "idx_accesslog_lab_time", columnList = "lab_id,entered_at")
})
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", nullable = false)
    private Lab lab;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "entered_at", nullable = false)
    private OffsetDateTime enteredAt;

    private OffsetDateTime exitedAt;
}

