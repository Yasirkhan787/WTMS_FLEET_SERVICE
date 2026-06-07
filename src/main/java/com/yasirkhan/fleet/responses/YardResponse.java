package com.yasirkhan.fleet.responses;

import com.yasirkhan.fleet.models.dtos.CoordinateDto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class YardResponse {

    private UUID yardId;
    private String yardName;
    private String yardType;
    private String status;

    // ADDED THESE:
    private UUID tehsilId;
    private String tehsilName;

    private String boundaryType;
    private CoordinateDto centerCoords;
    private Double radiusMeters;
    private String polygonPath;
}