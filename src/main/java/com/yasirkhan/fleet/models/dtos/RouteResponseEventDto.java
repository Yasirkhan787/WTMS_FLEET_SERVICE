package com.yasirkhan.fleet.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.EventType;
import com.yasirkhan.fleet.responses.RouteResponse;
import com.yasirkhan.fleet.responses.YardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteResponseEventDto {

    private EventType type;
    private EventStatus eventTypeStatus;
    private RouteResponse routeData;
    private YardResponse sourceYardData;
    private YardResponse destinationYardData;
}