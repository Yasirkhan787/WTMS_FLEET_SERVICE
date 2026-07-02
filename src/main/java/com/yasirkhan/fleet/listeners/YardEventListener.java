package com.yasirkhan.fleet.listeners;

import com.yasirkhan.fleet.models.dtos.DailyGoalResponseEventDto;
import com.yasirkhan.fleet.models.dtos.YardResponseEventDto;
import com.yasirkhan.fleet.producers.DailyGoalEventProducer;
import com.yasirkhan.fleet.producers.YardEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class YardEventListener {

    private final YardEventProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVehicleResponseEvent(YardResponseEventDto eventDto) {
        try {
            producer.sendYardResponseEvent(eventDto);
            log.info("Successfully published Kafka event for Yard: {}", eventDto.getYardData().getYardId());
        } catch (Exception e) {
            log.error("Failed to publish Kafka event for Tehsil: {}", eventDto.getYardData().getYardId(), e);
        }
    }
}