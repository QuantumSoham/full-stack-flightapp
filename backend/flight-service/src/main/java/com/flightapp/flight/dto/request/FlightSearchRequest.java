package com.flightapp.flight.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchRequest {

    @NotBlank(message = "From place is required")
    private String fromPlace;

    @NotBlank(message = "To place is required")
    private String toPlace;

    @NotNull(message = "Departure date is required")
    private LocalDateTime departureDate;

    private LocalDateTime returnDate;

    @NotNull(message = "Number of seats is required")
    @Positive(message = "Number of seats must be positive")
    private Integer numberOfSeats;

    private String tripType; // ONE_WAY or ROUND_TRIP
}
