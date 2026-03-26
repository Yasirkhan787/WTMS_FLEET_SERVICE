package com.yasirkhan.fleet.requests;

import com.yasirkhan.fleet.models.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequest {

    private String routeName;
    private String path;               // Encoded Polyline
    private String estimatedDistance;
    private String estimatedTime;
    private Status status;
}
