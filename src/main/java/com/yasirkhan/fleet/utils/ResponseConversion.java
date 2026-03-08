package com.yasirkhan.fleet.utils;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.entities.Driver;

public class ResponseConversion {

    public static DriverDto toDriverResponse(Driver driver){

        return DriverDto
                .builder()
                .userID(driver.getUserID())
                .name(driver.getName())
                .fatherName(driver.getFatherName())
                .email(driver.getEmail())
                .cnic(driver.getCnic())
                .phoneNo(driver.getPhoneNo())
                .address(driver.getAddress())
                .gender(driver.getGender())
                .licenseNo(driver.getLicenseNo())
                .licenseExpiry(driver.getLicenseExpiry())
                //.status(driver.setStatus())
                .build();

    }
}
