package com.yasirkhan.fleet.listeners;

import com.yasirkhan.fleet.models.dtos.RouteResponseEventDto;
import com.yasirkhan.fleet.producers.RouteEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteEventListener {

    private final RouteEventProducer producer;

    /**
     * Listens for RouteResponseEventDto events published by the RouteService.
     * The phase = TransactionPhase.AFTER_COMMIT ensures Kafka is only called
     * if the database transaction was successful.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRouteResponseEvent(RouteResponseEventDto eventDto) {
        try {
            producer.sendRouteResponseEvent(eventDto);
            log.info("Successfully published Kafka event for Route ID: {}", eventDto.getRouteData().getRouteId());
        } catch (Exception e) {
            // At this point, the DB commit is already done.
            // If Kafka is down, it won't roll back the database, but we MUST log it
            // deeply so an admin can manually reconcile the missed event,
            // or you can implement a retry mechanism (like Spring Retry) here.
            log.error("CRITICAL: Failed to publish Kafka event for Route ID: {}. Reason: {}",
                    eventDto.getRouteData().getRouteId(), e.getMessage(), e);
        }
    }
}