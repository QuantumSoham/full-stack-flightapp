package com.flightapp.booking.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

	@NotNull(message = "Flight ID is required")
	private Long flightId;

	@NotBlank(message = "User name is required")
	private String userName;

	@NotBlank(message = "User email is required")
	@Email(message = "Email should be valid")
	private String userEmail;

	@NotNull(message = "Number of seats is required")
	private Integer numberOfSeats;

	@NotEmpty(message = "At least one passenger is required")
	@Valid
	private List<PassengerRequest> passengers;
}
