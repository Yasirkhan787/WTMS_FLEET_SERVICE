package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.entities.Status;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface DriverService {

    void addDriver(DriverDto driverDto);

    List<DriverDto> getAllDrivers();

    DriverDto getDriverById(UUID userId);

    void updateStatus(UUID userID, Status status);
}
