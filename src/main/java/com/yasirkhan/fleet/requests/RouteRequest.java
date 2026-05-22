package com.yasirkhan.fleet.requests;

import com.yasirkhan.fleet.models.dtos.CoordinateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RouteRequest {

    @NotBlank(message = "Route name is required")
    private String routeName;

    @NotBlank(message = "Origin name is required")
    private String origin;

    @Valid
    @NotNull(message = "Origin coordinates are required")
    private CoordinateDto originCoords;

    @NotBlank(message = "Destination name is required")
    private String destination;

    @Valid
    @NotNull(message = "Destination coordinates are required")
    private CoordinateDto destinationCoords;

    @NotBlank(message = "Path data is required")
    private String path;

    @NotBlank(message = "Distance is required")
    private String estimatedDistance;

    @NotBlank(message = "Time is required")
    private String estimatedTime;
}