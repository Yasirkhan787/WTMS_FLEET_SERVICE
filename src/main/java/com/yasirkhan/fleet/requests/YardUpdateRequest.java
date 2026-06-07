package com.yasirkhan.fleet.requests;

import com.yasirkhan.fleet.models.enums.YardType;
import lombok.Data;

@Data
public class YardUpdateRequest {
    private String yardName;
    private YardType yardType;
    private String status;

    private String boundaryType; // "RADIUS" or "POLYGON"
    private Double centerLat;
    private Double centerLng;
    private Double radiusMeters;
    private String polygonPath;
}