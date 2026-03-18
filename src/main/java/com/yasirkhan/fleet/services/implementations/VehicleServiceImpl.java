package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.models.entities.Vehicle;
import com.yasirkhan.fleet.repositories.VehicleRepository;
import com.yasirkhan.fleet.requests.VehicleRequest;
import com.yasirkhan.fleet.responses.VehicleResponse;
import com.yasirkhan.fleet.services.VehicleService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional
    public VehicleResponse addVehicle(VehicleRequest request) {

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNo(request.getVehicleNo());
        vehicle.setModel(request.getModel());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setChassisNo(request.getChassisNo());
        vehicle.setEngineNo(request.getEngineNo());
        vehicle.setRegisteredTo(request.getRegisteredTo());

        Vehicle savedVehicle =
                null;
        try {
            savedVehicle = vehicleRepository.saveAndFlush(vehicle);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }

        return ResponseConversion
                .toVehicleResponse(savedVehicle);
    }

    @Override
    @Transactional
    public void updateVehicle(Map<String, Object> updates) {

        UUID vehicleId = UUID.fromString(updates.get("vehicleId").toString());

        Vehicle dbVehicle =
                vehicleRepository
                        .findById(vehicleId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Vehicle with ID: " + vehicleId +"Not Found"));

        updates.forEach((key, value) -> {
            switch (key){
                case "vehicleNo" ->  dbVehicle.setVehicleNo((String) value);
                case "model" -> dbVehicle.setModel((String) value);
                case "capacity" -> dbVehicle.setCapacity((float) value);
                case "chassisNo" -> dbVehicle.setChassisNo((String) value);
                case "engineNo" -> dbVehicle.setEngineNo((String) value);
                case "registeredTo" -> dbVehicle.setRegisteredTo((String) value);
            }
        });

        try {
            vehicleRepository.saveAndFlush(dbVehicle);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }


    }

    @Override
    public void blockVehicle(UUID vehicleId, Boolean blockStatus) {
        Vehicle dbVehicle =
                vehicleRepository
                        .findById(vehicleId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Vehicle with ID: " + vehicleId +"Not Found"));

        String status = blockStatus ? "BLOCKED" : "ACTIVE";
        dbVehicle.setStatus(Status.valueOf(status));

        try {
            vehicleRepository.saveAndFlush(dbVehicle);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }


    @Override
    public List<VehicleResponse> getAll() {

        List<Vehicle> vehicles =
                vehicleRepository.findAll();

        if (vehicles.isEmpty()) {
            throw new ResourceNotFoundException("No Vehicle Found in Database");
        }

        return vehicles
                .stream()
                .map(ResponseConversion::toVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleById(UUID vehicleId) {

        Vehicle vehicle =
                vehicleRepository
                        .findById(vehicleId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Vehicle with ID: " + vehicleId +"Not Found"));
        return ResponseConversion
                .toVehicleResponse(vehicle);
    }

}
