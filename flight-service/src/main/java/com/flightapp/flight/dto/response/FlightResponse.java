package com.flightapp.flight.dto.response;

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
public class FlightResponse {
    private Long id;
    private String flightNumber;
    private String fromPlace;
    private String toPlace;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private BigDecimal priceOneWay;
    private BigDecimal priceRoundTrip;
    private Integer totalSeats;
    private Integer availableSeats;
    private AirlineResponse airline;
}
