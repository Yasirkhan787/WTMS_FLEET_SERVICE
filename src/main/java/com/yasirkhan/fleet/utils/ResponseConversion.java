package com.yasirkhan.fleet.utils;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.entities.Driver;
import com.yasirkhan.fleet.responses.DriverResponse;

public class ResponseConversion {

    public static DriverResponse toDriverResponse(Driver driver){

        return DriverResponse
                .builder()
                .id(driver.getId())
                .name(driver.getName())
                .fatherName(driver.getFatherName())
                .email(driver.getEmail())
                .cnic(driver.getCnic())
                .phoneNo(driver.getPhoneNo())
                .address(driver.getAddress())
                .gender(driver.getGender())
                .licenseNo(driver.getLicenseNo())
                .licenseExpiry(driver.getLicenseExpiry())
                .status(driver.getStatus())
                .build();

    }
}
