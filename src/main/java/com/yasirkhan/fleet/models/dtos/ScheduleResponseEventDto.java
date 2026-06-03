package com.yasirkhan.fleet.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleResponseEventDto {

    private EventType type;
    private EventStatus eventTypeStatus;
    private String vehicleNo;
    private String vehicleStatus;
}
