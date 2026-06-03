package com.yasirkhan.fleet.consumers;

import com.yasirkhan.fleet.models.dtos.ScheduleResponseEventDto;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.Status;
import com.yasirkhan.fleet.repositories.VehicleRepository;
import com.yasirkhan.fleet.requests.VehicleUpdateRequest;
import com.yasirkhan.fleet.services.VehicleService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScheduleResponseEventConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final VehicleService vehicleService;

    public ScheduleResponseEventConsumer(RedisTemplate<String, Object> redisTemplate, VehicleService vehicleService) {
        this.redisTemplate = redisTemplate;
        this.vehicleService = vehicleService;
    }

    @KafkaListener(
            topics = "schedule-response-topic",
            groupId = "fleet-group",
            containerFactory = "listenerContainerFactory"
    )
    public void handleScheduleResponse(ScheduleResponseEventDto event) {

        if (EventStatus.SUCCESS.equals(event.getEventTypeStatus())) {

            // Update the primary PostgreSQL Database
            VehicleUpdateRequest vehicleUpdateRequest = VehicleUpdateRequest.builder()
                    .status(Status.valueOf(event.getVehicleStatus()))
                    .build();
            vehicleService.updateVehicle(event.getVehicleNo(), vehicleUpdateRequest);

            // Update the fast Redis Cache
            String redisVehicleKey = "wtms:vehicle:" + event.getVehicleNo();
            redisTemplate.opsForHash().put(redisVehicleKey, "status", event.getVehicleStatus());
        }
    }
}