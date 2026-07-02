package com.yasirkhan.fleet.producers;

import com.yasirkhan.fleet.models.dtos.DailyGoalResponseEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DailyGoalEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DailyGoalEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Send Yard Created/Updated Response Event
    public void sendDailyGoalResponseEvent(DailyGoalResponseEventDto eventDto) {
        kafkaTemplate.send("daily-goal-response-topic", eventDto).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("SUCCESS: Daily Goal Response {} event sent for Goal Id: {} (Partition: {}, Offset: {})",
                        eventDto.getType(),
                        eventDto.getGoalData().getGoalId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("FAILED to send Daily Goal Response {} event for Goal Id: {}. Reason: {}",
                        eventDto.getType(),
                        eventDto.getGoalData().getGoalId(),
                        ex.getMessage());
            }
        });
    }
}
