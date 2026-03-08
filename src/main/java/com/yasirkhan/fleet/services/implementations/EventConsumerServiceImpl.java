package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.dtos.DriverStatusChangedEventDto;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.services.DriverService;
import com.yasirkhan.fleet.services.EventConsumerService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EventConsumerServiceImpl implements EventConsumerService {

    private final DriverService driverService;

    public EventConsumerServiceImpl(DriverService driverService) {
        this.driverService = driverService;
    }

    @Override
    public void consumeDriverCreationEvent(DriverDto driverEventDto) {

        driverService.addDriver(driverEventDto);
    }

    @Override
    public void consumeDriverStatusEvent(DriverStatusChangedEventDto driverStatusChangedEventDto) {

        UUID id = driverStatusChangedEventDto.getId();

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", driverStatusChangedEventDto.getFleetStatus());

        driverService.updateDriver(id, updates);
    }
}
