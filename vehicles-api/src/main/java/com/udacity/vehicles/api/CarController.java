package com.udacity.vehicles.api;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarService;

import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Implements a REST-based controller for the Vehicles API.
 */

@RestController
@RequestMapping("/cars")
class CarController {
    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    /**
     * Creates a list to store any vehicles.
     * @return list of vehicles
     */
    @ApiResponses( value = {
        @ApiResponse(code = 200, message = "Car list request OK.")
    })
    @GetMapping
    ResponseEntity<?> listCars() {
        List<Resource<Car>> resources = carService.list().stream().map(assembler::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.accepted().body(resources);
    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @ApiResponses( value = {
        @ApiResponse(code = 400, message = "Car not found."),
        @ApiResponse(code = 200, message = "Car get request OK.")
    })
    @GetMapping("/{id}")
    ResponseEntity<?> findCar(@PathVariable Long id) {
        Car car = this.carService.findById(id);
        Resource<Car> resource = this.assembler.toResource(car);
        return ResponseEntity.ok().body(resource);
    }

    /**
     * Posts information to create a new vehicle in the system.
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @ApiResponses( value = {
        @ApiResponse(code = 201, message = "Car created successfully.")
    })
    @PostMapping
    ResponseEntity<?> createCar(@Valid @RequestBody Car car) throws URISyntaxException {
        Car savedCar = this.carService.save(car);
        Resource<Car> resource = this.assembler.toResource(savedCar);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    /**
     * Updates the information of a vehicle in the system.
     * @param id The ID number for which to update vehicle information.
     * @param car The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @ApiResponses( value = {
        @ApiResponse(code = 400, message = "Car not found."),
        @ApiResponse(code = 200, message = "Car updated successfully.")
    })
    @PutMapping("/{id}")
    ResponseEntity<?> updateCar(@PathVariable Long id, @Valid @RequestBody Car car) throws URISyntaxException{
        car.setId(id);
        this.carService.save(car);
        Resource<Car> resource = assembler.toResource(car);
        return ResponseEntity.ok().body(resource);
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCar(@PathVariable Long id) {
        this.carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
