package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.models.dtos.DriverDto;

public interface EventConsumerService {

    void consumeDriverCreationEvent(DriverDto driverEventDto);

    void consumeDriverStatusEvent(DriverDto driverEventDto);
}
