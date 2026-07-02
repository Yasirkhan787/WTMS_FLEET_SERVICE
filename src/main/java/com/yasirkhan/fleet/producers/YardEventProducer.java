package com.yasirkhan.fleet.producers;

import com.yasirkhan.fleet.models.dtos.YardResponseEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class YardEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public YardEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Send Yard Created/Updated Response Event
    public void sendYardResponseEvent(YardResponseEventDto eventDto) {
        kafkaTemplate.send("yard-response-topic", eventDto).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("SUCCESS: Yard Response {} event sent for Yard Id: {} (Partition: {}, Offset: {})",
                        eventDto.getType(),
                        eventDto.getYardData().getYardId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("FAILED to send Yard Response {} event for Yard Id: {}. Reason: {}",
                        eventDto.getType(),
                        eventDto.getYardData().getYardId(),
                        ex.getMessage());
            }
        });
    }
}
