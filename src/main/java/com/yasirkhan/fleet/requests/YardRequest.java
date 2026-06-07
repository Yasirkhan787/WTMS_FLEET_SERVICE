package com.yasirkhan.fleet.requests;

import com.yasirkhan.fleet.models.enums.YardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class YardRequest {

    @NotNull(message = "Tehsil ID is required")
    private UUID tehsilId; // ADDED THIS

    @NotBlank(message = "Yard name is required")
    private String yardName;

    @NotNull(message = "Yard type is required")
    private YardType yardType;

    @NotBlank(message = "Boundary type (RADIUS or POLYGON) is required")
    private String boundaryType;

    // --- Fields for RADIUS method ---
    private Double centerLat;
    private Double centerLng;
    private Double radiusMeters;

    // --- Field for POLYGON method ---
    private String polygonPath;
}