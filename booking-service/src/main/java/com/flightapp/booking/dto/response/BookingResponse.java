package com.flightapp.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {
	private Long id;
	private String pnr;
	private Long flightId;
	private String userName;
	private String userEmail;
	private Integer numberOfSeats;
	private LocalDateTime bookingDateTime;
	private LocalDateTime journeyDateTime;
	private String status;
	private BigDecimal totalPrice;
	private List<PassengerResponse> passengers;
}
