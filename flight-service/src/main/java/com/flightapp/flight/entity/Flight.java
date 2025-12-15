package com.flightapp.flight.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight",
       uniqueConstraints = @UniqueConstraint(name = "uq_flight_number_departure",
               columnNames = {"flight_number", "departure_datetime"}),
       indexes = {
           @Index(name = "idx_route", columnList = "from_place,to_place"),
           @Index(name = "idx_departure", columnList = "departure_datetime"),
           @Index(name = "idx_available_seats", columnList = "available_seats")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_flight_airline"))
    private Airline airline;

    @Column(name = "flight_number", nullable = false, length = 20)
    private String flightNumber;

    @Column(name = "from_place", nullable = false, length = 100)
    private String fromPlace;

    @Column(name = "to_place", nullable = false, length = 100)
    private String toPlace;

    @Column(name = "departure_datetime", nullable = false)
    private LocalDateTime departureDateTime;

    @Column(name = "arrival_datetime", nullable = false)
    private LocalDateTime arrivalDateTime;

    @Column(name = "price_one_way", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceOneWay;

    @Column(name = "price_round_trip", precision = 10, scale = 2)
    private BigDecimal priceRoundTrip;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business Methods
    public synchronized boolean hasAvailableSeats(Integer requiredSeats) {
        return this.availableSeats >= requiredSeats;
    }

    public synchronized void reduceAvailableSeats(Integer count) {
        if (this.availableSeats < count) {
            throw new RuntimeException("Insufficient seats available");
        }
        this.availableSeats -= count;
    }

    public synchronized void increaseAvailableSeats(Integer count) {
        this.availableSeats += count;
    }
}
