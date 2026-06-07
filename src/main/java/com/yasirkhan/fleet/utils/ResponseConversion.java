package com.yasirkhan.fleet.utils;

import com.google.maps.internal.PolylineEncoding;
import com.yasirkhan.fleet.models.dtos.CoordinateDto;
import com.yasirkhan.fleet.models.entities.*;
import com.yasirkhan.fleet.responses.*;

import java.util.stream.Collectors;

public class ResponseConversion {


    public static VehicleResponse toVehicleResponse(Vehicle savedVehicle) {

        return
                VehicleResponse
                        .builder()
                        .vehicleNo(savedVehicle.getVehicleNo())
                        .trackingId(savedVehicle.getTrackingId())
                        .model(savedVehicle.getModel())
                        .capacity(savedVehicle.getCapacity())
                        .chassisNo(savedVehicle.getChassisNo())
                        .engineNo(savedVehicle.getEngineNo())
                        .registeredTo(savedVehicle.getRegisteredTo())
                        .status(savedVehicle.getStatus())
                        .build();
    }

    // Inside your Fleet Service ResponseConversion.java
    public static RouteResponse toRouteResponse(Route route) {
        RouteResponse.RouteResponseBuilder builder = RouteResponse.builder()
                .routeId(route.getRouteId())
                .routeName(route.getRouteName())
                .status(route.getStatus().name())
                .estimatedDistance(route.getEstimatedDistance())
                .estimatedTime(route.getEstimatedTime())
                .path(route.getPath() != null ? SpatialUtils.toPolyLine(route.getPath()) : null);

        // Properly map the relationships
        if (route.getTehsil() != null) {
            builder.tehsilId(route.getTehsil().getTehsilId());
            builder.tehsilName(route.getTehsil().getTehsilName());
        }
        if (route.getSourceYard() != null) {
            builder.sourceYardId(route.getSourceYard().getId());
            builder.sourceYardName(route.getSourceYard().getYardName());
            builder.sourceYardType(String.valueOf(route.getSourceYard().getYardType()));
        }
        if (route.getDestinationYard() != null) {
            builder.destinationYardId(route.getDestinationYard().getId());
            builder.destinationYardName(route.getDestinationYard().getYardName());
            builder.destinationYardType(String.valueOf(route.getDestinationYard().getYardType()));
        }
        return builder.build();
    }

    public static TehsilResponse toTehsilResponse(Tehsil tehsil) {
        return TehsilResponse.builder()
                .tehsilId(tehsil.getTehsilId())
                .tehsilName(tehsil.getTehsilName())
                .yards(tehsil.getYards() != null ?
                        tehsil.getYards().stream()
                                .map(ResponseConversion::toYardResponse)
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    public static YardResponse toYardResponse(Yard yard) {
        YardResponse.YardResponseBuilder builder = YardResponse.builder()
                .yardId(yard.getId())
                .yardName(yard.getYardName())
                .yardType(yard.getYardType().name())
                .status(yard.getStatus().name());

        // ADDED TEHSIL MAPPING HERE:
        if (yard.getTehsil() != null) {
            builder.tehsilId(yard.getTehsil().getTehsilId());
            builder.tehsilName(yard.getTehsil().getTehsilName());
        }

        // Check which geometry type exists in the database
        if (yard.getCenterPoint() != null) {
            builder.boundaryType("RADIUS");
            builder.radiusMeters(yard.getRadiusMeters());
            builder.centerCoords(CoordinateDto.builder()
                    .lat(yard.getCenterPoint().getY())
                    .lng(yard.getCenterPoint().getX())
                    .build());
        } else if (yard.getBoundaryPolygon() != null) {
            builder.boundaryType("POLYGON");
            builder.polygonPath(SpatialUtils.toPolyLine(yard.getBoundaryPolygon().getExteriorRing()));
        }

        return builder.build();
    }

    // Inside your ResponseConversion class in Fleet Service
    public static DailyGoalResponse toDailyGoalResponse(DailyGoal goal) {
        return DailyGoalResponse.builder()
                .goalId(goal.getGoalId())
                .tehsilId(goal.getTehsil().getTehsilId()) // Extract from the linked Tehsil entity
                .tehsilName(goal.getTehsil().getTehsilName()) // Pass name to frontend
                .targetDate(goal.getTargetDate())
                .targetTonnage(goal.getTargetTonnage())
                .assignedBy(goal.getAssignedBy())
                .build();
    }
}
