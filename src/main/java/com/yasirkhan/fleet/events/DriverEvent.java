package com.yasirkhan.fleet.events;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.dtos.DriverStatusChangedEventDto;
import com.yasirkhan.fleet.services.EventConsumerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DriverEvent {

    private final EventConsumerService eventConsumerService;

    public DriverEvent(EventConsumerService eventConsumerService) {

        this.eventConsumerService = eventConsumerService;
    }

    @KafkaListener(
            topics = "user-creation-driver-topic",
            groupId = "fleet-group",
            containerFactory = "listenerContainerFactory" // This must match your @Bean name
    )
    public void consumeDriverCreationEvent(DriverDto driverEventDto){
        eventConsumerService.consumeDriverCreationEvent(driverEventDto);
    }

    @KafkaListener(
            topics = "user-status-driver-topic",
            groupId = "fleet-group",
            containerFactory = "listenerContainerFactory" // This must match your @Bean name
    )
    public void consumeDriverStatusEvent(DriverStatusChangedEventDto driverStatusChangedEventDto){
        eventConsumerService.consumeDriverStatusEvent(driverStatusChangedEventDto);
    }


}
