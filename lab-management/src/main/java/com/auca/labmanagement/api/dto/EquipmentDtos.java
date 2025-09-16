package com.auca.labmanagement.api.dto;

import com.auca.labmanagement.domain.EquipmentStatus;
import jakarta.validation.constraints.*;

public class EquipmentDtos {
    public record CreateEquipmentRequest(
            @NotBlank @Size(max = 100) String assetTag,
            @NotBlank @Size(max = 200) String model,
            @NotNull Long labId
    ) {}

    public record UpdateEquipmentRequest(
            @Size(max = 200) String model,
            EquipmentStatus status
    ) {}

    public record EquipmentResponse(Long id, String assetTag, String model, EquipmentStatus status, Long labId) {}
}

