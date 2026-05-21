package com.yasirkhan.fleet.producers;

import com.yasirkhan.fleet.models.dtos.RouteResponseEventDto;
import com.yasirkhan.fleet.models.dtos.VehicleResponseEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RouteEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RouteEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Send Route Created/Updated Response Event
    public void sendRouteResponseEvent(RouteResponseEventDto eventDto) {
        kafkaTemplate.send("route-response-topic", eventDto).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("SUCCESS: Route Response {} event sent for Route Id: {} (Partition: {}, Offset: {})",
                        eventDto.getType(),
                        eventDto.getRouteId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("FAILED to send Route Response {} event for Route Id: {}. Reason: {}",
                        eventDto.getType(),
                        eventDto.getRouteId(),
                        ex.getMessage());
            }
        });
    }
}
