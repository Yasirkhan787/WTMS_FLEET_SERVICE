package com.yasirkhan.fleet.listeners;

import com.yasirkhan.fleet.models.dtos.VehicleResponseEventDto;
import com.yasirkhan.fleet.producers.VehicleEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleEventListener {

    private final VehicleEventProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVehicleResponseEvent(VehicleResponseEventDto eventDto) {
        try {
            producer.sendVehicleResponseEvent(eventDto);
            log.info("Successfully published Kafka event for Vehicle: {}", eventDto.getVehicleData().getVehicleNo());
        } catch (Exception e) {
            // Note: Since DB is already committed, if Kafka fails here,
            // you might want to log it deeply or implement a retry mechanism.
            log.error("Failed to publish Kafka event for Vehicle: {}", eventDto.getVehicleData().getVehicleNo(), e);
        }
    }
}