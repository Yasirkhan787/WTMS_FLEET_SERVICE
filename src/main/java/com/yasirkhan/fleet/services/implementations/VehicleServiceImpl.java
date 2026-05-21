package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.exceptions.ResourceAlreadyExistException;
import com.yasirkhan.fleet.models.dtos.VehicleResponseEventDto;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.models.entities.Vehicle;
import com.yasirkhan.fleet.producers.VehicleEventProducer;
import com.yasirkhan.fleet.repositories.VehicleRepository;
import com.yasirkhan.fleet.requests.VehicleRequest;
import com.yasirkhan.fleet.responses.VehicleResponse;
import com.yasirkhan.fleet.services.VehicleService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import jakarta.transaction.Transactional;
import com.yasirkhan.fleet.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    private final VehicleEventProducer producer;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleEventProducer producer) {
        this.vehicleRepository = vehicleRepository;
        this.producer = producer;
    }

    @Override
    @Transactional
    public VehicleResponse addVehicle(VehicleRequest request) {

        if (vehicleRepository.existsById(request.getVehicleNo())) {
            throw new
                    ResourceAlreadyExistException(
                            "Vehicle with Vehicle No" + request.getVehicleNo() + " is Already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNo(request.getVehicleNo());
        vehicle.setModel(request.getModel());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setChassisNo(request.getChassisNo());
        vehicle.setEngineNo(request.getEngineNo());
        vehicle.setRegisteredTo(request.getRegisteredTo());
        vehicle.setStatus(Status.ACTIVE);

        Vehicle savedVehicle =
                null;
        try {
            savedVehicle = vehicleRepository.saveAndFlush(vehicle);
            // Send event to kafka
            VehicleResponseEventDto eventDto =
                    VehicleResponseEventDto
                            .builder()
                            .vehicleNo(savedVehicle.getVehicleNo())
                            .status("SUCCESS")
                            .type("CREATE")
                            .message("Vehicle added successfully")
                            .build();
            producer.sendVehicleResponseEvent(eventDto);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }

        return ResponseConversion
                .toVehicleResponse(savedVehicle);
    }

    @Override
    @Transactional
    public void updateVehicle(Map<String, Object> updates) {

        String vehicleNo = updates.get("vehicleNo").toString();

        Vehicle dbVehicle =
                vehicleRepository
                        .findById(vehicleNo)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Vehicle with Vehicle No: " + vehicleNo +" Not Found"));

        updates.forEach((key, value) -> {
            if (value == null) return; // Prevent NullPointerExceptions

            switch (key){
                case "updatedVehicleNo" -> {
                    if (vehicleRepository.existsById(value.toString())) {
                        throw new
                                ResourceAlreadyExistException(
                                "Vehicle with Vehicle No" + value.toString() + "Already exists");
                    }
                    dbVehicle.setVehicleNo(value.toString());
                }
                case "model" -> dbVehicle.setModel(value.toString());
                case "capacity" -> dbVehicle.setCapacity(((Number) value).floatValue());
                case "chassisNo" -> dbVehicle.setChassisNo(value.toString());
                case "engineNo" -> dbVehicle.setEngineNo(value.toString());
                case "registeredTo" -> dbVehicle.setRegisteredTo(value.toString());
                case "status" -> dbVehicle.setStatus(Status.valueOf(value.toString()));
            }
        });

        try {
            vehicleRepository.saveAndFlush(dbVehicle);

            // Send event to kafka
            VehicleResponseEventDto eventDto =
                    VehicleResponseEventDto
                            .builder()
                            .vehicleNo(dbVehicle.getVehicleNo())
                            .status("SUCCESS")
                            .type("UPDATE")
                            .message("Vehicle updated successfully")
                            .build();
            producer.sendVehicleResponseEvent(eventDto);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Override
    public void blockVehicle(String vehicleNo, Boolean blockStatus) {
        Vehicle dbVehicle =
                vehicleRepository
                        .findById(vehicleNo)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Vehicle with Vehicle No: " + vehicleNo +"Not Found"));

        String status = blockStatus ? "BLOCKED" : "ACTIVE";
        dbVehicle.setStatus(Status.valueOf(status));

        try {
            vehicleRepository.saveAndFlush(dbVehicle);
            // Send event to kafka
            VehicleResponseEventDto eventDto =
                    VehicleResponseEventDto
                            .builder()
                            .vehicleNo(dbVehicle.getVehicleNo())
                            .status(status)
                            .type("STATUS_UPDATE")
                            .message("Vehicle Status{ " + status + " } updated successfully")
                            .build();
            producer.sendVehicleResponseEvent(eventDto);
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
    public VehicleResponse getVehicleById(String vehicleNo) {

        Vehicle vehicle =
                vehicleRepository
                        .findById(vehicleNo)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Vehicle with Vehicle No: " + vehicleNo +"Not Found"));
        return ResponseConversion
                .toVehicleResponse(vehicle);
    }

}
