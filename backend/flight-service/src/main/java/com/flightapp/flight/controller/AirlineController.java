package com.flightapp.flight.controller;

import com.flightapp.flight.dto.request.AddAirlineRequest;
import com.flightapp.flight.dto.response.ApiResponse;
import com.flightapp.flight.dto.response.AirlineResponse;
import com.flightapp.flight.service.AirlineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/flight/airline")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
		RequestMethod.DELETE, RequestMethod.PUT })
public class AirlineController {

	private final AirlineService airlineService;

	@PostMapping("/add")
	public ResponseEntity<ApiResponse<AirlineResponse>> addAirline(@Valid @RequestBody AddAirlineRequest request) {
		try {
			log.info("Received request to add airline: {}", request.getCode());
			AirlineResponse airline = airlineService.addAirline(request);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(ApiResponse.success("Airline added successfully", airline));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
		} catch (Exception e) {
			log.error("Error adding airline", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("Error adding airline: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AirlineResponse>> getAirline(@PathVariable Long id) {
		try {
			AirlineResponse airline = airlineService.getAirline(id);
			return ResponseEntity.ok(ApiResponse.success("Airline fetched successfully", airline));
		} catch (Exception e) {
			log.error("Error fetching airline", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
		}
	}

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<AirlineResponse>>> getAllAirlines() {
		try {
			List<AirlineResponse> airlines = airlineService.getAllAirlines();
			return ResponseEntity.ok(ApiResponse.success("Airlines fetched successfully", airlines));
		} catch (Exception e) {
			log.error("Error fetching airlines", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("Error fetching airlines: " + e.getMessage()));
		}
	}
}
