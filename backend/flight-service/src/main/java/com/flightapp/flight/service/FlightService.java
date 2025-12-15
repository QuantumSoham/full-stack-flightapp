package com.flightapp.flight.service;

import com.flightapp.flight.dto.request.AddFlightInventoryRequest;
import com.flightapp.flight.dto.request.FlightSearchRequest;
import com.flightapp.flight.dto.response.AirlineResponse;
import com.flightapp.flight.dto.response.FlightResponse;
import com.flightapp.flight.dto.response.FlightSearchResultDto;
import com.flightapp.flight.entity.Airline;
import com.flightapp.flight.entity.Flight;
import com.flightapp.flight.exception.InsufficientSeatsException;
import com.flightapp.flight.exception.InvalidFlightDataException;
import com.flightapp.flight.exception.ResourceNotFoundException;
import com.flightapp.flight.repository.AirlineRepository;
import com.flightapp.flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;

    @Transactional
    public FlightResponse addFlightInventory(AddFlightInventoryRequest request) {
        log.info("Adding flight inventory: {}", request.getFlightNumber());

        // Validate datetime
        if (request.getArrivalDateTime().isBefore(request.getDepartureDateTime())) {
            throw new InvalidFlightDataException("Arrival time cannot be before departure time");
        }

        // Fetch airline
        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new ResourceNotFoundException("Airline not found with ID: " + request.getAirlineId()));

        Flight flight = Flight.builder()
                .airline(airline)
                .flightNumber(request.getFlightNumber())
                .fromPlace(request.getFromPlace())
                .toPlace(request.getToPlace())
                .departureDateTime(request.getDepartureDateTime())
                .arrivalDateTime(request.getArrivalDateTime())
                .priceOneWay(request.getPriceOneWay())
                .priceRoundTrip(request.getPriceRoundTrip())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .isActive(true)
                .build();

        Flight savedFlight = flightRepository.save(flight);
        log.info("Flight added successfully with ID: {}", savedFlight.getId());

        return convertToFlightResponse(savedFlight);
    }

    @Transactional(readOnly = true)
    public FlightSearchResultDto searchFlights(FlightSearchRequest request) {
        log.info("Searching flights from {} to {} on {}", 
                request.getFromPlace(), request.getToPlace(), request.getDepartureDate());

        FlightSearchResultDto result = new FlightSearchResultDto();

        List<FlightResponse> outboundFlights = flightRepository.searchOneWayFlights(
                request.getFromPlace(),
                request.getToPlace(),
                request.getDepartureDate()
        ).stream()
                .filter(f -> f.getAvailableSeats() >= request.getNumberOfSeats())
                .map(this::convertToFlightResponse)
                .collect(Collectors.toList());

        result.setOutboundFlights(outboundFlights);
        result.setTripType(request.getTripType());

        // Handle round trip
        if ("ROUND_TRIP".equals(request.getTripType()) && request.getReturnDate() != null) {
            List<FlightResponse> returnFlights = flightRepository.searchRoundTripReturn(
                    request.getToPlace(),
                    request.getFromPlace(),
                    request.getReturnDate()
            ).stream()
                    .filter(f -> f.getAvailableSeats() >= request.getNumberOfSeats())
                    .map(this::convertToFlightResponse)
                    .collect(Collectors.toList());

            result.setReturnFlights(returnFlights);
        }

        log.info("Found {} outbound flights and {} return flights", 
                outboundFlights.size(), 
                result.getReturnFlights() != null ? result.getReturnFlights().size() : 0);

        return result;
    }

    @Transactional(readOnly = true)
    public FlightResponse getFlight(Long flightId) {
        log.info("Fetching flight with ID: {}", flightId);
        Flight flight = flightRepository.findByIdAndIsActiveTrue(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));
        return convertToFlightResponse(flight);
    }

    @Transactional
    public void reduceAvailableSeats(Long flightId, Integer seatsCount) {
        log.info("Reducing available seats for flight ID: {} by count: {}", flightId, seatsCount);

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));

        if (!flight.hasAvailableSeats(seatsCount)) {
            throw new InsufficientSeatsException("Not enough available seats for flight: " + flightId);
        }

        flight.reduceAvailableSeats(seatsCount);
        flightRepository.save(flight);
        log.info("Seats reduced successfully. Remaining seats: {}", flight.getAvailableSeats());
    }

    @Transactional
    public void increaseAvailableSeats(Long flightId, Integer seatsCount) {
        log.info("Increasing available seats for flight ID: {} by count: {}", flightId, seatsCount);

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));

        flight.increaseAvailableSeats(seatsCount);
        flightRepository.save(flight);
        log.info("Seats increased successfully. Available seats: {}", flight.getAvailableSeats());
    }

    private FlightResponse convertToFlightResponse(Flight flight) {
        Airline airline = flight.getAirline();
        AirlineResponse airlineResponse = AirlineResponse.builder()
                .id(airline.getId())
                .name(airline.getName())
                .code(airline.getCode())
                .logoUrl(airline.getLogoUrl())
                .build();

        return FlightResponse.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .fromPlace(flight.getFromPlace())
                .toPlace(flight.getToPlace())
                .departureDateTime(flight.getDepartureDateTime())
                .arrivalDateTime(flight.getArrivalDateTime())
                .priceOneWay(flight.getPriceOneWay())
                .priceRoundTrip(flight.getPriceRoundTrip())
                .totalSeats(flight.getTotalSeats())
                .availableSeats(flight.getAvailableSeats())
                .airline(airlineResponse)
                .build();
    }
}
