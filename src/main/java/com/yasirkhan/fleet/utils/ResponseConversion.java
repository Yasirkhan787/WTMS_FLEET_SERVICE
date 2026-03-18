package com.yasirkhan.fleet.utils;

import com.yasirkhan.fleet.models.entities.Vehicle;
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
}
