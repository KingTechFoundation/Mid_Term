package com.auca.labmanagement.api.dto;

import com.auca.labmanagement.domain.MaintenanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MaintenanceDtos {
    public record CreateMaintenanceRequest(
            @NotNull Long equipmentId,
            @NotBlank String description
    ) {}

    public record UpdateMaintenanceRequest(
            MaintenanceStatus status,
            Long assignedToId
    ) {}

    public record MaintenanceResponse(Long id, Long equipmentId, Long requestedById, Long assignedToId, MaintenanceStatus status, String description) {}
}

