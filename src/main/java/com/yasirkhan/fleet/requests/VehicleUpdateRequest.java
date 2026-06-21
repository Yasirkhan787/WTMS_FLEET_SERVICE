package com.yasirkhan.fleet.requests;

import com.yasirkhan.fleet.models.enums.Status;
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
public class VehicleUpdateRequest {

    private String model;
    private String trackingId;
    private Float capacity; // Wrapper class to allow nulls
    private String engineNo;
    private String chassisNo;
    private String registeredTo;
    @NotNull
    private UUID tehsilId;
    private Status status;
}