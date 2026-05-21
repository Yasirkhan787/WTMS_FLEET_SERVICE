package com.yasirkhan.fleet.producers;

import com.yasirkhan.fleet.models.dtos.VehicleResponseEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VehicleEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public VehicleEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Send Vehicle Created/Updated Response Event
    public void sendVehicleResponseEvent(VehicleResponseEventDto eventDto) {
        kafkaTemplate.send("vehicle-response-topic", eventDto).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("SUCCESS: Vehicle Response {} event sent for Vehicle NO: {} (Partition: {}, Offset: {})",
                        eventDto.getType(),
                        eventDto.getVehicleNo(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("FAILED to send Vehicle Response {} event for Vehicle NO: {}. Reason: {}",
                        eventDto.getType(),
                        eventDto.getVehicleNo(),
                        ex.getMessage());
            }
        });
    }
}
