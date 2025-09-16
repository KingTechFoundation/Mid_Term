package com.auca.labmanagement.api.dto;

import com.auca.labmanagement.domain.BookingType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class BookingDtos {
    public record CreateBookingRequest(
            @NotNull Long labId,
            @NotNull BookingType type,
            @NotNull @Future OffsetDateTime startTime,
            @NotNull @Future OffsetDateTime endTime,
            @NotBlank String purpose
    ) {}

    public record UpdateBookingRequest(
            @Future OffsetDateTime startTime,
            @Future OffsetDateTime endTime,
            String purpose
    ) {}

    public record BookingResponse(Long id, Long labId, Long createdById, BookingType type, OffsetDateTime startTime, OffsetDateTime endTime, String purpose) {}
}

