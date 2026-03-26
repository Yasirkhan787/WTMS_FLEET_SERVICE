package com.yasirkhan.fleet.utils;

import com.google.maps.internal.PolylineEncoding;
import com.yasirkhan.fleet.models.entities.Route;
import com.yasirkhan.fleet.models.entities.Vehicle;
import com.yasirkhan.fleet.responses.RouteResponse;
import com.yasirkhan.fleet.responses.VehicleResponse;

public class ResponseConversion {


    public static VehicleResponse toVehicleResponse(Vehicle savedVehicle) {

        return
                VehicleResponse
                        .builder()
                        .vehicleId(savedVehicle.getVehicleId())
                        .vehicleNo(savedVehicle.getVehicleNo())
                        .model(savedVehicle.getModel())
                        .capacity(savedVehicle.getCapacity())
                        .chassisNo(savedVehicle.getChassisNo())
                        .engineNo(savedVehicle.getEngineNo())
                        .registeredTo(savedVehicle.getRegisteredTo())
                        .status(savedVehicle.getStatus())
                        .build();
    }

    public static RouteResponse toRouteResponse(Route savedRoute){

        return
                RouteResponse
                        .builder()
                        .routeId(savedRoute.getRouteId())
                        .routeName(savedRoute.getRouteName())
                        .path(SpatialUtils.toPolyLine(savedRoute.getPath()))
                        .estimatedTime(savedRoute.getEstimatedTime())
                        .estimatedDistance(savedRoute.getEstimatedDistance())
                        .status(savedRoute.getStatus())
                        .build();
    }
}
