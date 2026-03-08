package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.entities.Driver;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.repositories.DriverRepository;
import com.yasirkhan.fleet.services.DriverService;
import com.yasirkhan.fleet.utils.EntityConversion;
import com.yasirkhan.fleet.utils.ResponseConversion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // (Through Event)
    @Override
    public void addDriver(DriverDto driverDto) {

        // Convert Dto -> Driver Entity
        Driver driver =
                EntityConversion.toDriverEntity(driverDto);

        // Save To Database
        Driver savedDriver =
                driverRepository.save(driver);
    }

    // (Through Event)
    @Override
    public void updateDriver(DriverDto driverDto){

        // Find Driver
        Driver dbDriver =
                driverRepository.findByUserId(driverDto.getUserID())
                        .orElseThrow(
                                () ->  new RuntimeException
                                        ("Driver with ID: " + driverDto.getUserID()
                                                + "Not Found"));

        // Convert Dto -> Driver Entity
        dbDriver.setName(driverDto.getName());
        dbDriver.setFatherName(driverDto.getFatherName());
        dbDriver.setEmail(driverDto.getEmail());
        dbDriver.setCnic(driverDto.getCnic());
        dbDriver.setPhoneNo(driverDto.getPhoneNo());
        dbDriver.setAddress(driverDto.getAddress());
        dbDriver.setGender(driverDto.getGender());
        dbDriver.setLicenseNo(driverDto.getLicenseNo());
        dbDriver.setLicenseExpiry(driverDto.getLicenseExpiry());

        // Save To Database
        Driver updatedDriver =
                driverRepository.save(dbDriver);
    }

    // Blocked or Un-Blocked (Through Event)
    public void updateBStatus(UUID userID, Status status){

        // Find Driver
        Driver dbDriver =
                driverRepository.findByUserId(userID)
                        .orElseThrow(
                                () ->  new RuntimeException
                                        ("Driver with ID: " + userID
                                                + "Not Found"));
        dbDriver.setStatus(status);
    }


    // Change Status (Direct API)
    @Override
    public void updateStatus(UUID userID, Status status){

        // Find Driver
        Driver dbDriver =
                driverRepository.findByUserId(userID)
                        .orElseThrow(
                                () ->  new RuntimeException
                                        ("Driver with ID: " + userID
                                                + "Not Found"));

        dbDriver.setStatus(status);
    }

    @Override
    public List<DriverDto> getAllDrivers() {

        List<Driver> drivers =
                driverRepository.findAll();

        if (drivers.isEmpty()) {
            throw new RuntimeException("No Driver Found in Database");
        }

        return
                drivers
                        .stream()
                        .map(ResponseConversion::toDriverResponse)
                        .collect(Collectors.toList());
    }

    @Override
    public DriverDto getDriverById(UUID userId){

        Driver dbDriver =
                driverRepository.findByUserId(userId)
                        .orElseThrow(
                                () ->  new RuntimeException
                                        ("Driver with ID: " + userId
                                                + "Not Found"));

        return
                ResponseConversion.toDriverResponse(dbDriver);
    }
}
