package com.yasirkhan.fleet.responses;

import com.yasirkhan.fleet.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private String vehicleNo;
    private String trackingId;
    private String model;
    private float capacity;
    private String engineNo;
    private String chassisNo;
    private String registeredTo;
    private UUID tehsilId;
    private String tehsilName;
    private Status status;
}
