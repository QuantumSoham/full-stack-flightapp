package com.flightapp.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "booking", indexes = { @Index(name = "idx_pnr", columnList = "pnr"),
		@Index(name = "idx_user_email", columnList = "user_email"),
		@Index(name = "idx_booking_date", columnList = "booking_datetime") })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//I am Generating safe equals/hashCode without including passengers to avoid infinite recursion and JPA performance issues

@EqualsAndHashCode(exclude = "passengers")
@ToString(exclude = "passengers")
public class Booking {

	public enum Status {
		BOOKED, CANCELLED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Long id;

	@Column(name = "pnr", nullable = false, length = 20, unique = true)
	private String pnr;

	@Column(name = "flight_id", nullable = false)
	private Long flightId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_booking_user"))
	private UserAccount user;

	@Column(name = "user_name", nullable = false, length = 100)
	private String userName;

	@Column(name = "user_email", nullable = false, length = 100)
	private String userEmail;

	@Column(name = "number_of_seats", nullable = false)
	private Integer numberOfSeats;

	@Column(name = "booking_datetime", nullable = false, updatable = false)
	private LocalDateTime bookingDateTime;

	@Column(name = "journey_datetime", nullable = false)
	private LocalDateTime journeyDateTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private Status status;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Passenger> passengers;

	@Column(name = "total_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalPrice;

	@Version
	private Long version;

	@PrePersist
	protected void onCreate() {
		if (bookingDateTime == null) {
			bookingDateTime = LocalDateTime.now();
		}
	}

	// Business Methods
	public boolean canBeCancelled() {
		return LocalDateTime.now().isBefore(journeyDateTime.minusHours(24));
	}

	public String getStatusDisplay() {
		return status.toString();
	}
}
