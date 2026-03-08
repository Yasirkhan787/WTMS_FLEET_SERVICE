package com.yasirkhan.fleet.utils;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.entities.Driver;
import com.yasirkhan.fleet.models.entities.Status;

public class EntityConversion {

    public static Driver toDriverEntity(DriverDto driverDto){

        Driver driver =
                new Driver();
        driver.setUserID(driverDto.getUserID());
        driver.setName(driverDto.getName());
        driver.setFatherName(driverDto.getFatherName());
        driver.setEmail(driverDto.getEmail());
        driver.setCnic(driverDto.getCnic());
        driver.setPhoneNo(driverDto.getPhoneNo());
        driver.setAddress(driverDto.getAddress());
        driver.setGender(driverDto.getGender());
        driver.setLicenseNo(driverDto.getLicenseNo());
        driver.setLicenseExpiry(driverDto.getLicenseExpiry());
        driver.setStatus(Status.ACTIVE);

        return driver;
    }
}
