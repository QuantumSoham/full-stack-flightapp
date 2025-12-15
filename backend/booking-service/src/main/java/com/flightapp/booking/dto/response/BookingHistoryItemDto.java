package com.flightapp.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingHistoryItemDto {
	private Long id;
	private String pnr;
	private Long flightId;
	private LocalDateTime bookingDateTime;
	private LocalDateTime journeyDateTime;
	private String status;
	private Integer numberOfSeats;
	private BigDecimal totalPrice;
}
