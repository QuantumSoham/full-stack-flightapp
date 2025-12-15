package com.flightapp.flight.controller;

import com.flightapp.flight.dto.request.AddFlightInventoryRequest;
import com.flightapp.flight.dto.request.FlightSearchRequest;
import com.flightapp.flight.dto.response.ApiResponse;
import com.flightapp.flight.dto.response.FlightResponse;
import com.flightapp.flight.dto.response.FlightSearchResultDto;
import com.flightapp.flight.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/airline/inventory/add")
    public ResponseEntity<ApiResponse<FlightResponse>> addFlightInventory(@Valid @RequestBody AddFlightInventoryRequest request) {
        try {
            log.info("Received request to add flight inventory: {}", request.getFlightNumber());
            FlightResponse flight = flightService.addFlightInventory(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Flight added to inventory successfully", flight));
        } catch (Exception e) {
            log.error("Error adding flight", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<FlightSearchResultDto>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        try {
            log.info("Searching flights from {} to {}", request.getFromPlace(), request.getToPlace());
            FlightSearchResultDto result = flightService.searchFlights(request);
            return ResponseEntity.ok(ApiResponse.success("Flights found successfully", result));
        } catch (Exception e) {
            log.error("Error searching flights", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error searching flights: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightResponse>> getFlight(@PathVariable Long id) {
        try {
            FlightResponse flight = flightService.getFlight(id);
            return ResponseEntity.ok(ApiResponse.success("Flight fetched successfully", flight));
        } catch (Exception e) {
            log.error("Error fetching flight", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/reduce-seats")
    public ResponseEntity<ApiResponse<String>> reduceSeat(@PathVariable Long id, @RequestParam Integer count) {
        try {
            flightService.reduceAvailableSeats(id, count);
            return ResponseEntity.ok(ApiResponse.success("Seats reduced successfully", ""));
        } catch (Exception e) {
            log.error("Error reducing seats", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/increase-seats")
    public ResponseEntity<ApiResponse<String>> increaseSeat(@PathVariable Long id, @RequestParam Integer count) {
        try {
            flightService.increaseAvailableSeats(id, count);
            return ResponseEntity.ok(ApiResponse.success("Seats increased successfully", ""));
        } catch (Exception e) {
            log.error("Error increasing seats", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
