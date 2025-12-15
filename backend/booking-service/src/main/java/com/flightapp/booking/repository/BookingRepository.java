package com.flightapp.booking.repository;

import com.flightapp.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPnr(String pnr);

    @Query("SELECT b FROM Booking b WHERE b.userEmail = :email ORDER BY b.bookingDateTime DESC")
    List<Booking> findByUserEmailOrderByBookingDateTimeDesc(@Param("email") String email);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.bookingDateTime DESC")
    List<Booking> findByUserIdOrderByBookingDateTimeDesc(@Param("userId") Long userId);
}
