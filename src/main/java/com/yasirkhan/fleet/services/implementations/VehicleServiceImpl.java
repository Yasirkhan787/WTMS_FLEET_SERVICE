package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.exceptions.DataBaseException;
import com.yasirkhan.fleet.exceptions.ResourceAlreadyExistException;
import com.yasirkhan.fleet.exceptions.ResourceNotFoundException;
import com.yasirkhan.fleet.models.dtos.VehicleResponseEventDto;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.EventType;
import com.yasirkhan.fleet.models.enums.Status;
import com.yasirkhan.fleet.models.entities.Vehicle;
import com.yasirkhan.fleet.repositories.VehicleRepository;
import com.yasirkhan.fleet.requests.VehicleRequest;
import com.yasirkhan.fleet.requests.VehicleUpdateRequest;
import com.yasirkhan.fleet.responses.VehicleResponse;
import com.yasirkhan.fleet.services.VehicleService;
import com.yasirkhan.fleet.utils.ResponseConversion;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate; // Injected Redis

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              ApplicationEventPublisher eventPublisher,
                              RedisTemplate<String, Object> redisTemplate) {
        this.vehicleRepository = vehicleRepository;
        this.eventPublisher = eventPublisher;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public VehicleResponse addVehicle(VehicleRequest request) {
        if (vehicleRepository.existsById(request.getVehicleNo())) {
            throw new ResourceAlreadyExistException(
                    "Vehicle with Vehicle No " + request.getVehicleNo() + " already exists.");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNo(request.getVehicleNo());
        vehicle.setTrackingId(request.getTrackingId());
        vehicle.setModel(request.getModel());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setChassisNo(request.getChassisNo());
        vehicle.setEngineNo(request.getEngineNo());
        vehicle.setRegisteredTo(request.getRegisteredTo());
        vehicle.setStatus(Status.ACTIVE);

        try {
            Vehicle savedVehicle = vehicleRepository.saveAndFlush(vehicle);

            // Sync directly to local Redis cache
            syncVehicleToRedis(savedVehicle);

            VehicleResponse response = ResponseConversion.toVehicleResponse(savedVehicle);

            // Broadcast so OTHER services can update their caches
            publishVehicleEvent(EventType.CREATE, EventStatus.SUCCESS, response);
            return response;

        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to save vehicle: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateVehicle(String vehicleNo, VehicleUpdateRequest request) {
        Vehicle dbVehicle = vehicleRepository.findById(vehicleNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle with Vehicle No " + vehicleNo + " not found."));

        if (request.getTrackingId() != null) dbVehicle.setTrackingId(request.getTrackingId());
        if (request.getModel() != null) dbVehicle.setModel(request.getModel());
        if (request.getCapacity() != null) dbVehicle.setCapacity(request.getCapacity());
        if (request.getChassisNo() != null) dbVehicle.setChassisNo(request.getChassisNo());
        if (request.getEngineNo() != null) dbVehicle.setEngineNo(request.getEngineNo());
        if (request.getRegisteredTo() != null) dbVehicle.setRegisteredTo(request.getRegisteredTo());
        if (request.getStatus() != null) dbVehicle.setStatus(request.getStatus());

        try {
            Vehicle updatedVehicle = vehicleRepository.saveAndFlush(dbVehicle);

            // Sync the updated state to Redis
            syncVehicleToRedis(updatedVehicle);

            VehicleResponse response = ResponseConversion.toVehicleResponse(updatedVehicle);
            publishVehicleEvent(EventType.UPDATE, EventStatus.SUCCESS, response);

        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to update vehicle: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void blockVehicle(String vehicleNo, Boolean blockStatus) {
        Vehicle dbVehicle = vehicleRepository.findById(vehicleNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle with Vehicle No " + vehicleNo + " not found."));

        dbVehicle.setStatus(blockStatus ? Status.BLOCKED : Status.ACTIVE);

        try {
            Vehicle updatedVehicle = vehicleRepository.saveAndFlush(dbVehicle);

            // Sync the new status to Redis
            syncVehicleToRedis(updatedVehicle);

            VehicleResponse response = ResponseConversion.toVehicleResponse(updatedVehicle);
            publishVehicleEvent(EventType.UPDATE, EventStatus.SUCCESS, response);

        } catch (DataAccessException e) {
            throw new DataBaseException("Failed to block vehicle: " + e.getMessage());
        }
    }

    @Override
    public List<VehicleResponse> getAll() {
        return vehicleRepository.findAll()
                .stream()
                .map(ResponseConversion::toVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleById(String vehicleNo) {
        Vehicle vehicle = vehicleRepository.findById(vehicleNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle with Vehicle No " + vehicleNo + " not found."));
        return ResponseConversion.toVehicleResponse(vehicle);
    }

    // --- Redis Sync Helper (Vehicle) ---
    private void syncVehicleToRedis(Vehicle vehicle) {
        String redisKey = "wtms:vehicle:" + vehicle.getVehicleNo();
        Map<String, Object> data = new HashMap<>();

        // Convert everything to String to ensure safe cross-service serialization
        data.put("model", vehicle.getModel());
        data.put("capacity", String.valueOf(vehicle.getCapacity()));
        data.put("engineNo", vehicle.getEngineNo());
        data.put("chassisNo", vehicle.getChassisNo());
        data.put("registeredTo", vehicle.getRegisteredTo());
        //data.put("averageKmPerLiter", String.valueOf(vehicle.getAverageKmPerLiter()));
        data.put("status", vehicle.getStatus().name()); // .name() is safer for Enums than .toString()

        redisTemplate.opsForHash().putAll(redisKey, data);
    }

    // --- Kafka Publisher Helper ---
    private void publishVehicleEvent(EventType type, EventStatus status, VehicleResponse data) {
        VehicleResponseEventDto eventDto = VehicleResponseEventDto.builder()
                .type(type)
                .eventTypeStatus(status)
                .vehicleData(data)
                .build();
        eventPublisher.publishEvent(eventDto);
    }
}