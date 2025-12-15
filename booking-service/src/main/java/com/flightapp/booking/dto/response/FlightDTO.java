package com.flightapp.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {
	private Long id;
	private String flightNumber;
	private String fromPlace;
	private String toPlace;
	private BigDecimal priceOneWay;
	private BigDecimal priceRoundTrip;
	private Integer availableSeats;
}
