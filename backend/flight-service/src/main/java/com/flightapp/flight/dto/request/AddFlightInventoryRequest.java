package com.flightapp.flight.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddFlightInventoryRequest {

    @NotNull(message = "Airline ID is required")
    @Positive(message = "Airline ID must be positive")
    private Long airlineId;

    @NotBlank(message = "Flight number is required")
    @Size(min = 1, max = 20, message = "Flight number must be between 1 and 20 characters")
    private String flightNumber;

    @NotBlank(message = "From place is required")
    @Size(min = 1, max = 100, message = "From place must be between 1 and 100 characters")
    private String fromPlace;

    @NotBlank(message = "To place is required")
    @Size(min = 1, max = 100, message = "To place must be between 1 and 100 characters")
    private String toPlace;

    @NotNull(message = "Departure date-time is required")
    @Future(message = "Departure date-time must be in the future")
    private LocalDateTime departureDateTime;

    @NotNull(message = "Arrival date-time is required")
    private LocalDateTime arrivalDateTime;

    @NotNull(message = "Price one way is required")
    @Positive(message = "Price must be positive")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal priceOneWay;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal priceRoundTrip;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;
}
