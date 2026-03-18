package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.requests.VehicleRequest;
import com.yasirkhan.fleet.responses.VehicleResponse;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VehicleService {

    VehicleResponse addVehicle(VehicleRequest request);

    List<VehicleResponse> getAll();

    VehicleResponse getVehicleById(UUID vehicleId);

    void updateVehicle(Map<String, Object> updates);

    void blockVehicle(UUID vehicleId, Boolean blockStatus);
}
