package com.yasirkhan.fleet.responses;

import com.yasirkhan.fleet.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private String vehicleNo;
    private String model;
    private float capacity;
    private String engineNo;
    private String chassisNo;
    private String registeredTo;
    private Status status;
}
