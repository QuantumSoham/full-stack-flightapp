package com.flightapp.booking.dto.request;

import com.flightapp.booking.entity.Passenger;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerRequest {

	@NotBlank(message = "Passenger name is required")
	private String name;

	@NotNull(message = "Gender is required")
	private Passenger.Gender gender;

	@NotNull(message = "Age is required")
	@Positive(message = "Age must be positive")
	private Integer age;

	@NotBlank(message = "Seat number is required")
	private String seatNumber;

	@NotNull(message = "Meal preference is required")
	private Passenger.MealType mealType;
}
