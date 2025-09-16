package com.auca.labmanagement.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class AccessDtos {
    public record LogAccessRequest(
            @NotNull Long labId,
            @NotNull Long userId,
            OffsetDateTime enteredAt,
            OffsetDateTime exitedAt
    ) {}

    public record AccessResponse(Long id, Long labId, Long userId, OffsetDateTime enteredAt, OffsetDateTime exitedAt) {}
}

