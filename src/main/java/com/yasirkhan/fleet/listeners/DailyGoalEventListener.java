package com.yasirkhan.fleet.listeners;

import com.yasirkhan.fleet.models.dtos.DailyGoalResponseEventDto;
import com.yasirkhan.fleet.models.dtos.VehicleResponseEventDto;
import com.yasirkhan.fleet.producers.DailyGoalEventProducer;
import com.yasirkhan.fleet.producers.VehicleEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyGoalEventListener {

    private final DailyGoalEventProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVehicleResponseEvent(DailyGoalResponseEventDto eventDto) {
        try {
            producer.sendDailyGoalResponseEvent(eventDto);
            log.info("Successfully published Kafka event for Tehsil: {}", eventDto.getGoalData().getTehsilId());
        } catch (Exception e) {
            log.error("Failed to publish Kafka event for Tehsil: {}", eventDto.getGoalData().getTehsilId(), e);
        }
    }
}