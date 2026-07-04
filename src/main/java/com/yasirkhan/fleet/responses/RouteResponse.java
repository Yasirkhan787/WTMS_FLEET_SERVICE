package com.yasirkhan.fleet.responses;

import com.yasirkhan.fleet.models.dtos.CoordinateDto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RouteResponse {
    private UUID routeId;
    private String routeName;
    private UUID tehsilId;
    private String tehsilName;
    private UUID sourceYardId;
    private String sourceYardName;
    private String sourceYardType;
    private UUID destinationYardId;
    private String destinationYardType;
    private String destinationYardName;
    private String path;
    private String estimatedDistance;
    private String estimatedTime;
    private String status;

}