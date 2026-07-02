package com.yasirkhan.fleet.producers;

import com.yasirkhan.fleet.models.dtos.TehsilResponseEventDto;
import com.yasirkhan.fleet.models.dtos.YardResponseEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TehsilEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TehsilEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTehsilResponseEvent(TehsilResponseEventDto eventDto) {
        kafkaTemplate.send("tehsil-response-topic", eventDto).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("SUCCESS: Tehsil Response {} event sent for Tehsil Id: {} (Partition: {}, Offset: {})",
                        eventDto.getType(),
                        eventDto.getTehsilData().getTehsilId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("FAILED to send Tehsil Response {} event for Tehsil Id: {}. Reason: {}",
                        eventDto.getType(),
                        eventDto.getTehsilData().getTehsilId(),
                        ex.getMessage());
            }
        });
    }
}
