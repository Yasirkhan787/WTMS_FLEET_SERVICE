package com.yasirkhan.fleet.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "WTMS_VEHICLES")
public class Vehicle {

    @Id
    private String vehicleNo;

    private String model;

    private float capacity;

    private String engineNo;

    private String chassisNo;

    private String registeredTo;

    private Status status;

}
