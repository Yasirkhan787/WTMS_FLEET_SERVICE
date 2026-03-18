package com.yasirkhan.fleet.responses;

import com.yasirkhan.fleet.models.entities.Status;
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

    private UUID vehicleId;
    private String vehicleNo;
    private String model;
    private float capacity;
    private String engineNo;
    private String chassisNo;
    private String registeredTo;
    private Status status;
}
