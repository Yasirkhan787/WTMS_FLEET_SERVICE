package com.yasirkhan.fleet.services.implementations;

import com.yasirkhan.fleet.models.dtos.DriverDto;
import com.yasirkhan.fleet.models.dtos.DriverStatusChangedEventDto;
import com.yasirkhan.fleet.models.entities.Driver;
import com.yasirkhan.fleet.models.entities.Status;
import com.yasirkhan.fleet.repositories.DriverRepository;
import com.yasirkhan.fleet.services.DriverService;
import com.yasirkhan.fleet.utils.EntityConversion;
import com.yasirkhan.fleet.utils.ResponseConversion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public void addDriver(DriverDto driverDto) {

        // Convert Dto -> Driver Entity
        Driver driver =
                EntityConversion.toDriverEntity(driverDto);

        // Save To Database
        Driver savedDriver =
                driverRepository.save(driver);
    }

    @Override
    public void updateDriver(UUID userID, Map<String, Object> updates) {

        Driver dbDriver =
                driverRepository.findByUserId(userID)
                        .orElseThrow(
                                () ->  new RuntimeException
                                        ("Driver with ID: " + userID + "Not Found"));

        updates.forEach((key, value) ->
                {
                    switch (key){
                        case "name" -> dbDriver.setName((String) value);
                        case "fatherName" -> dbDriver.setFatherName((String) value);
                        case "email" -> dbDriver.setEmail((String) value);
                        case "cnic" -> dbDriver.setCnic((String) value);
                        case "gender" -> dbDriver.setGender((String) value);
                        case "phoneNo" -> dbDriver.setPhoneNo((String) value);
                        case "address" -> dbDriver.setAddress((String) value);
                        case "licenseNo" -> dbDriver.setLicenseNo((String) value);
                        case "licenseExpiry" -> dbDriver.setLicenseExpiry(LocalDate.parse((String) value));
                        case "status" -> dbDriver.setStatus(Status.valueOf((String) value));
                    }
                }
        );
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
    public DriverDto getDriverById(UUID userID){

        Driver dbDriver =
                driverRepository.findByUserId(userID)
                        .orElseThrow(
                                () ->  new RuntimeException
                                        ("Driver with ID: " + userID
                                                + "Not Found"));

        return
                ResponseConversion.toDriverResponse(dbDriver);
    }

    // Blocked or Un-Blocked (Through Event)
    @Override
    public void toggleDriverStatus(DriverStatusChangedEventDto driverStatusChangedEventDto){

        // Find Driver
        Driver dbDriver =
                driverRepository.findByUserId(driverStatusChangedEventDto.getUserID())
                        .orElseThrow(
                                () ->  new RuntimeException
                                        ("Driver with ID: "
                                                + driverStatusChangedEventDto
                                                .getUserID()
                                                + "Not Found"));
        dbDriver.setStatus(Status.valueOf(driverStatusChangedEventDto.getFleetStatus()));
    }
}
