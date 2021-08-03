package com.udacity.vehicles.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import com.udacity.vehicles.client.maps.Address;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;

import org.modelmapper.ModelMapper;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
//@AllArgsConstructor
@EnableEurekaClient
@Service
public class CarService {

    private final CarRepository repository;
    private final MapsClient maps;
    private final PriceClient pricing;

    public CarService(CarRepository repository, ModelMapper mapper, WebClient maps, WebClient pricing) {
        this.repository = repository;
        this.maps = new MapsClient(maps, mapper);
        this.pricing = new PriceClient(pricing);
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        List<Car> cars = repository.findAll();
        cars.forEach(car -> {
                            setLocation(car);
                            setPrice(car);
                            }
                    );
        return cars;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) throws CarNotFoundException {
        Optional<Car> responseCar = repository.findById(id);

        if (responseCar.isEmpty()) throw new CarNotFoundException(MessageFormat.format("Car {0} not found.", id));

        Car car = responseCar.get();
        setLocation(car);
        setPrice(car);

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        Car updatedCar;

        if (car.getId() != null) {
            updatedCar = repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        } else {
            updatedCar = car;
        }

        updatedCar = repository.save(updatedCar);

        setLocation(updatedCar);
        setPrice(updatedCar);

        return updatedCar;
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) throws CarNotFoundException {
        Optional<Car> responseCar = repository.findById(id);
        if (responseCar.isEmpty()) throw new CarNotFoundException(MessageFormat.format("Car {0} not found.", id));

        repository.deleteById(id);
    }

    private void setLocation(Car car) {
        Location location = this.maps.getAddress(car.getLocation());
        car.setLocation(location);

        // ClientResponse responseMap = this.maps.get()
        //                                         .uri(uriBuilder -> uriBuilder
        //                                             .path("/maps")
        //                                             .queryParam("lat", "{lat}")
        //                                             .queryParam("lon", "{lon}")
        //                                             .build(location.getLat(), location.getLon()))
        //                                         .accept(MediaType.APPLICATION_JSON)
        //                                         .exchange()
        //                                         .block();

        // Address address = responseMap.bodyToMono(Address.class).block();
        // location.setAddress(address.getAddress());
        // location.setCity(address.getCity());
        // location.setState(address.getState());
        // location.setZip(address.getZip());

        // car.setLocation(location);
    }

    private void setPrice(Car car) {
        String price = this.pricing.getPrice(car.getId());
        car.setPrice(price);

        // Long id = car.getId();
        // ClientResponse responsePrice = this.pricing.get()
        //                                             .uri(uriBuilder -> uriBuilder
        //                                                 .path("/services/price")
        //                                                 .queryParam("vehicleId", "{vehicleId}")
        //                                                 .build(id))
        //                                             .accept(MediaType.APPLICATION_JSON)
        //                                             .exchange()
        //                                             .block();

        // Price price = responsePrice.bodyToMono(Price.class).block();
        // car.setPrice(price.toString());
    }

}
