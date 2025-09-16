package com.auca.labmanagement.api.dto;

import com.auca.labmanagement.domain.LabType;
import jakarta.validation.constraints.*;

public class LabDtos {
    public record CreateLabRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Size(max = 100) String location,
            @NotNull @Min(1) Integer capacity,
            @NotNull LabType type,
            Long managerId
    ) {}

    public record UpdateLabRequest(
            @Size(max = 100) String location,
            @Min(1) Integer capacity,
            LabType type,
            Long managerId
    ) {}

    public record LabResponse(Long id, String name, String location, Integer capacity, LabType type, Long managerId) {}
}

