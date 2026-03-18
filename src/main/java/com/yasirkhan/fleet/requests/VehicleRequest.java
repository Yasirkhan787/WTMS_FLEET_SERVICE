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
public class VehicleRequest {

    private String vehicleNo;
    private String model;
    private float capacity;
    private String engineNo;
    private String chassisNo;
    private String registeredTo;
    private Status status;
}
