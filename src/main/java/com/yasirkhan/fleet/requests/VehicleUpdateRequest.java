package com.yasirkhan.fleet.requests;

import com.yasirkhan.fleet.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateRequest {

    private String model;
    private Float capacity; // Wrapper class to allow nulls
    private String engineNo;
    private String chassisNo;
    private String registeredTo;
    private Status status;
}