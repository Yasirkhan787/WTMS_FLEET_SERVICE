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

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private float capacity;

    @Column(nullable = false, unique = true)
    private String engineNo;

    @Column(nullable = false, unique = true)
    private String chassisNo;

    @Column(nullable = false)
    private String registeredTo;

    @Enumerated(EnumType.STRING)
    private Status status;

}
