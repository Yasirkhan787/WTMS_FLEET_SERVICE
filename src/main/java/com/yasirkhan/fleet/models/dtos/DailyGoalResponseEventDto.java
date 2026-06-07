package com.yasirkhan.fleet.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.EventType;
import com.yasirkhan.fleet.responses.DailyGoalResponse;
import com.yasirkhan.fleet.responses.RouteResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyGoalResponseEventDto {

    private EventType type;
    private EventStatus eventTypeStatus;
    private DailyGoalResponse goalData;
}