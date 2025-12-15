package com.flightapp.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
//i am creating indexes on frequently used columns , heard its best practice
@Table(name = "passenger", uniqueConstraints = @UniqueConstraint(name = "uq_booking_seat", columnNames = { "booking_id",
		"seat_number" }), indexes = { @Index(name = "idx_booking_passenger", columnList = "booking_id"),
				@Index(name = "idx_flight_passenger", columnList = "flight_id") })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

	public enum Gender {
		MALE, FEMALE, OTHER
	}

	public enum MealType {
		VEG, NON_VEG
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "passenger_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id", nullable = false, foreignKey = @ForeignKey(name = "fk_passenger_booking"))
	private Booking booking;

	@Column(name = "flight_id", nullable = false)
	private Long flightId;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", nullable = false, length = 10)
	private Gender gender;

	@Column(name = "age", nullable = false)
	private Integer age;

	@Column(name = "seat_number", nullable = false, length = 10)
	private String seatNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "meal_type", nullable = false, length = 10)
	private MealType mealType;
}
