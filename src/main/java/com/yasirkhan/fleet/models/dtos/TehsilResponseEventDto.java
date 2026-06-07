package com.yasirkhan.fleet.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yasirkhan.fleet.models.enums.EventStatus;
import com.yasirkhan.fleet.models.enums.EventType;
import com.yasirkhan.fleet.responses.TehsilResponse;
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
public class TehsilResponseEventDto {

    private EventType type;
    private EventStatus eventTypeStatus;
    private TehsilResponse tehsilData;
}