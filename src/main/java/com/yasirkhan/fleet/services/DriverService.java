package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.dtos.DriverStatusChangedEventDto;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.responses.DriverResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DriverService {

    void addDriver(DriverDto driverDto);

    void updateDriver(UUID id, Map<String, Object> updates);

    List<DriverResponse> getAllDrivers();

    DriverResponse getDriverById(UUID id);

    //void toggleDriverStatus(DriverStatusChangedEventDto driverStatusChangedEventDto);

}
