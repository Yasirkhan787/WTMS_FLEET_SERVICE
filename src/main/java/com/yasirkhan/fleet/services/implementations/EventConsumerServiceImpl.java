package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.services.DriverService;
import com.yasirkhan.fleet.services.EventConsumerService;
import org.springframework.stereotype.Service;

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
    public void consumeDriverUpdationEvent(DriverDto driverEventDto) {
        driverService.updateDriver(driverEventDto);
    }

    @Override
    public void consumeDriverStatusEvent(DriverDto driverEventDto) {
        driverService.updateStatus(driverEventDto.getUserID(), Status.BLOCKED);
    }
}
