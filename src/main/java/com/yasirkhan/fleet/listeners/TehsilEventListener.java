package com.yasirkhan.fleet.listeners;

import com.yasirkhan.fleet.models.dtos.DailyGoalResponseEventDto;
import com.yasirkhan.fleet.models.dtos.TehsilResponseEventDto;
import com.yasirkhan.fleet.producers.DailyGoalEventProducer;
import com.yasirkhan.fleet.producers.TehsilEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TehsilEventListener {

    private final TehsilEventProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVehicleResponseEvent(TehsilResponseEventDto eventDto) {
        try {
            producer.sendTehsilResponseEvent(eventDto);
            log.info("Successfully published Kafka event for Tehsil: {}", eventDto.getTehsilData().getTehsilName());
        } catch (Exception e) {
            log.error("Failed to publish Kafka event for Tehsil: {}", eventDto.getTehsilData().getTehsilName(), e);
        }
    }
}