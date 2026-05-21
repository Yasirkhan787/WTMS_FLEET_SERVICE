package com.yasirkhan.fleet.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseEventDto {

    private String vehicleNo;

    private String status;  // SUCCESS, FAILURE  in-case of STATUS_UPDATE(ACTUAL STATUS e.g., ACTIVE, BLOCKED etc...)

    private String type;  // CREATE, UPDATE, STATUS_UPDATE

    private String message;
}