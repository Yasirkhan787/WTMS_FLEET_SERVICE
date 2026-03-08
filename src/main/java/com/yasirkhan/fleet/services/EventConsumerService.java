package com.yasirkhan.fleet.services;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.dtos.DriverStatusChangedEventDto;

public interface EventConsumerService {

    void consumeDriverCreationEvent(DriverDto driverEventDto);

    void consumeDriverStatusEvent(DriverStatusChangedEventDto driverStatusChangedEventDto);
}
