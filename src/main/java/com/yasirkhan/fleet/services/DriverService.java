package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.dtos.DriverStatusChangedEventDto;
import com.yasirkhan.fleet.models.entities.Status;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DriverService {

    void addDriver(DriverDto driverDto);

    void updateDriver(UUID userID, Map<String, Object> updates);

    List<DriverDto> getAllDrivers();

    DriverDto getDriverById(UUID userID);

    void toggleDriverStatus(DriverStatusChangedEventDto driverStatusChangedEventDto);

}
