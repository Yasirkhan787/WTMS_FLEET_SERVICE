package com.yasirkhan.fleet.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class DailyGoalRequest {

    @NotNull(message = "Tehsil ID is required")
    private UUID tehsilId;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    @NotNull(message = "Target tonnage is required")
    @Positive(message = "Tonnage must be greater than zero")
    private Double targetTonnage;

    @NotBlank(message = "Admin assignment name is required")
    private String assignedBy;
}