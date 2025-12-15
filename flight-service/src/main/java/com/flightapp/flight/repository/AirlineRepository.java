package com.flightapp.flight.repository;

import com.flightapp.flight.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
    Optional<Airline> findByCode(String code);
    
    Optional<Airline> findByCodeAndIsActiveTrue(String code);
    
    @Query("SELECT a FROM Airline a WHERE a.isActive = true ORDER BY a.name")
    List<Airline> findAllActive();
}
