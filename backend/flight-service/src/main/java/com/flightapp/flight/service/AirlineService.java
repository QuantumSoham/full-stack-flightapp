package com.flightapp.flight.service;

import com.flightapp.flight.dto.request.AddAirlineRequest;
import com.flightapp.flight.dto.response.AirlineResponse;
import com.flightapp.flight.entity.Airline;
import com.flightapp.flight.exception.ResourceNotFoundException;
import com.flightapp.flight.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository airlineRepository;

    @Transactional
    public AirlineResponse addAirline(AddAirlineRequest request) {
        log.info("Adding new airline with code: {}", request.getCode());

        if (airlineRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Airline with code " + request.getCode() + " already exists");
        }

        Airline airline = Airline.builder()
                .name(request.getName())
                .code(request.getCode())
                .logoUrl(request.getLogoUrl())
                .isActive(true)
                .build();

        Airline savedAirline = airlineRepository.save(airline);
        log.info("Airline added successfully with ID: {}", savedAirline.getId());

        return convertToResponse(savedAirline);
    }

    @Transactional(readOnly = true)
    public AirlineResponse getAirline(Long id) {
        log.info("Fetching airline with ID: {}", id);
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airline not found with ID: " + id));
        return convertToResponse(airline);
    }

    @Transactional(readOnly = true)
    public List<AirlineResponse> getAllAirlines() {
        log.info("Fetching all active airlines");
        return airlineRepository.findAllActive().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private AirlineResponse convertToResponse(Airline airline) {
        return AirlineResponse.builder()
                .id(airline.getId())
                .name(airline.getName())
                .code(airline.getCode())
                .logoUrl(airline.getLogoUrl())
                .build();
    }
}
