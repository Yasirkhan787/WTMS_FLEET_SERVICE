package com.yasirkhan.fleet.utils;

import com.google.maps.internal.PolylineEncoding;
import com.yasirkhan.fleet.models.dtos.CoordinateDto;
import com.yasirkhan.fleet.models.entities.Route;
import com.yasirkhan.fleet.models.entities.Vehicle;
import com.yasirkhan.fleet.responses.RouteResponse;
import com.yasirkhan.fleet.responses.VehicleResponse;

public class ResponseConversion {


    public static VehicleResponse toVehicleResponse(Vehicle savedVehicle) {

        return
                VehicleResponse
                        .builder()
                        .vehicleNo(savedVehicle.getVehicleNo())
                        .model(savedVehicle.getModel())
                        .capacity(savedVehicle.getCapacity())
                        .chassisNo(savedVehicle.getChassisNo())
                        .engineNo(savedVehicle.getEngineNo())
                        .registeredTo(savedVehicle.getRegisteredTo())
                        .status(savedVehicle.getStatus())
                        .build();
    }

    public static RouteResponse toRouteResponse(Route route) {
        return RouteResponse.builder()
                .routeId(route.getRouteId())
                .routeName(route.getRouteName())

                .origin(route.getOrigin())
                .originCoords(CoordinateDto.builder()
                        .lat(route.getOriginLat())
                        .lng(route.getOriginLng())
                        .build())

                .destination(route.getDestination())
                .destinationCoords(CoordinateDto.builder()
                        .lat(route.getDestinationLat())
                        .lng(route.getDestinationLng())
                        .build())

                .estimatedDistance(route.getEstimatedDistance())
                .estimatedTime(route.getEstimatedTime())
                .status(route.getStatus().name())
                .path(SpatialUtils.toPolyLine(route.getPath()))
                .build();
    }
}
