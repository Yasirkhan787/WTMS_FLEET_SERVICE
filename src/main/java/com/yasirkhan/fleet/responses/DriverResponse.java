package com.yasirkhan.fleet.responses;

import com.yasirkhan.fleet.models.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {

    private UUID id;

    private String name;

    private String fatherName;

    private String email;

    private String cnic;

    private String phoneNo;

    private String address;

    private String gender;

    private String licenseNo;

    private LocalDate licenseExpiry;

    private Status status;

}
