package com.yasirkhan.fleet.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {

    private String vehicleNo;
    private String trackingId;
    private String model;
    private float capacity;
    private String engineNo;
    private String chassisNo;
    private String registeredTo;
    private double mileage;
    @NotNull
    private UUID tehsilId;
}
