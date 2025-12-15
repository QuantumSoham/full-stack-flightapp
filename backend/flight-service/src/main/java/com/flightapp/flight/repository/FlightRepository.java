package com.flightapp.flight.repository;

import com.flightapp.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM Flight f WHERE f.fromPlace = :from AND f.toPlace = :to " +
           "AND DATE(f.departureDateTime) = DATE(:departureDate) AND f.isActive = true " +
           "AND f.availableSeats > 0 ORDER BY f.departureDateTime ASC")
    List<Flight> searchOneWayFlights(@Param("from") String from, 
                                     @Param("to") String to,
                                     @Param("departureDate") LocalDateTime departureDate);

    @Query("SELECT f FROM Flight f WHERE f.fromPlace = :from AND f.toPlace = :to " +
           "AND DATE(f.departureDateTime) = DATE(:departureDate) " +
           "AND f.isActive = true AND f.availableSeats > 0 ORDER BY f.departureDateTime ASC")
    List<Flight> searchRoundTripOutbound(@Param("from") String from, 
                                         @Param("to") String to,
                                         @Param("departureDate") LocalDateTime departureDate);

    @Query("SELECT f FROM Flight f WHERE f.fromPlace = :to AND f.toPlace = :from " +
           "AND DATE(f.departureDateTime) = DATE(:returnDate) " +
           "AND f.isActive = true AND f.availableSeats > 0 ORDER BY f.departureDateTime ASC")
    List<Flight> searchRoundTripReturn(@Param("to") String to, 
                                       @Param("from") String from,
                                       @Param("returnDate") LocalDateTime returnDate);

    Optional<Flight> findByIdAndIsActiveTrue(Long id);

    @Query("SELECT f FROM Flight f WHERE f.id = :flightId AND f.availableSeats >= :seatsRequired " +
           "AND f.isActive = true")
    Optional<Flight> findAvailableFlight(@Param("flightId") Long flightId, 
                                        @Param("seatsRequired") Integer seatsRequired);
}
