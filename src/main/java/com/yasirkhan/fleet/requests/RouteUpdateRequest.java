package com.yasirkhan.fleet.requests;

import com.yasirkhan.fleet.models.dtos.CoordinateDto;
import com.yasirkhan.fleet.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteUpdateRequest {
    // Note: Use wrapper classes (e.g., Boolean, Double) if any primitives exist
    // so they default to null rather than 0/false during PATCH requests.
    private String routeName;
    private String origin;
    private CoordinateDto originCoords;
    private String destination;
    private CoordinateDto destinationCoords;
    private String path;
    private String estimatedTime;
    private String estimatedDistance;
    private Status status;
}