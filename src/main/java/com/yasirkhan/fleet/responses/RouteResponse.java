package com.yasirkhan.fleet.responses;

import com.yasirkhan.fleet.models.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {

    private UUID routeId;
    private String routeName;
    private String path;               // Encoded Polyline
    private String estimatedDistance;
    private String estimatedTime;
    private Status status;
}
