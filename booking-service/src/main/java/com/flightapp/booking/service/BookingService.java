package com.flightapp.booking.service;

import com.flightapp.booking.client.FlightServiceClient;
import com.flightapp.booking.dto.request.BookingRequest;
import com.flightapp.booking.dto.request.PassengerRequest;
import com.flightapp.booking.dto.response.*;
import com.flightapp.booking.entity.Booking;
import com.flightapp.booking.entity.Passenger;
import com.flightapp.booking.entity.UserAccount;
import com.flightapp.booking.exception.BookingCancellationException;
import com.flightapp.booking.exception.FlightServiceException;
import com.flightapp.booking.exception.ResourceNotFoundException;
import com.flightapp.booking.repository.BookingRepository;
import com.flightapp.booking.repository.PassengerRepository;
import com.flightapp.booking.repository.UserAccountRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final UserAccountRepository userAccountRepository;
    private final FlightServiceClient flightServiceClient;

    private static final String FLIGHT_CB = "flightService";

    @Transactional
    public BookingResponse bookFlight(BookingRequest request) {
        log.info("Processing flight booking for user: {} with {} seats",
                request.getUserEmail(), request.getNumberOfSeats());

        // Verify flight exists and has seats (protected by circuit breaker)
        FlightDTO flightDetails = fetchFlightDetailsWithCircuitBreaker(request.getFlightId());

        if (flightDetails.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new FlightServiceException("Insufficient available seats for booking");
        }

        // Get or create user
        UserAccount user = userAccountRepository.findByEmail(request.getUserEmail())
                .orElse(null);

        // Generate unique PNR
        String pnr = generatePNR();

        // Calculate total price
        BigDecimal totalPrice = flightDetails.getPriceOneWay()
                .multiply(new BigDecimal(request.getNumberOfSeats()));

        // Create booking
        Booking booking = Booking.builder()
                .pnr(pnr)
                .flightId(request.getFlightId())
                .user(user)
                .userName(request.getUserName())
                .userEmail(request.getUserEmail())
                .numberOfSeats(request.getNumberOfSeats())
                .status(Booking.Status.BOOKED)
                .journeyDateTime(LocalDateTime.now().plusDays(7)) // Default 7 days ahead
                .totalPrice(totalPrice)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with PNR: {}", pnr);

        // Add passengers
        List<Passenger> passengers = request.getPassengers().stream()
                .map(passengerReq -> createPassenger(passengerReq, savedBooking, request.getFlightId()))
                .collect(Collectors.toList());

        passengerRepository.saveAll(passengers);
        savedBooking.setPassengers(passengers);

        // Reduce available seats in Flight Service (best-effort)
        try {
            flightServiceClient.reduceFlightSeats(request.getFlightId(), request.getNumberOfSeats());
            log.info("Seats reduced in Flight Service");
        } catch (Exception e) {
            log.error("Error reducing seats in Flight Service, but booking is created", e);
            // In production, consider compensating actions or rollback
        }

        return convertToBookingResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingByPNR(String pnr) {
        log.info("Fetching booking with PNR: {}", pnr);
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with PNR: " + pnr));
        return convertToBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingHistoryItemDto> getBookingHistory(String email) {
        log.info("Fetching booking history for email: {}", email);
        List<Booking> bookings = bookingRepository.findByUserEmailOrderByBookingDateTimeDesc(email);

        return bookings.stream()
                .map(b -> BookingHistoryItemDto.builder()
                        .id(b.getId())
                        .pnr(b.getPnr())
                        .flightId(b.getFlightId())
                        .bookingDateTime(b.getBookingDateTime())
                        .journeyDateTime(b.getJourneyDateTime())
                        .status(b.getStatus().toString())
                        .numberOfSeats(b.getNumberOfSeats())
                        .totalPrice(b.getTotalPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse cancelBooking(String pnr) {
        log.info("Cancelling booking with PNR: {}", pnr);

        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with PNR: " + pnr));

        // Check if cancellation is allowed (24 hours before journey)
        if (!booking.canBeCancelled()) {
            throw new BookingCancellationException(
                    "Cancellation not allowed. Must cancel at least 24 hours before journey. Journey date: " + booking.getJourneyDateTime()
            );
        }

        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new BookingCancellationException("Booking is already cancelled");
        }

        booking.setStatus(Booking.Status.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        // Increase seats back in Flight Service
        try {
            flightServiceClient.increaseFlightSeats(booking.getFlightId(), booking.getNumberOfSeats());
            log.info("Seats increased back in Flight Service after cancellation");
        } catch (Exception e) {
            log.error("Error increasing seats in Flight Service after cancellation", e);
        }

        return convertToBookingResponse(cancelledBooking);
    }

    /**
     * Wrapper around Feign client call with circuit-breaker protection.
     * Fallback method will be invoked if the call fails or circuit is open.
     */
//    @CircuitBreaker(name = FLIGHT_CB, fallbackMethod = "flightDetailsFallback")
//    public FlightDTO fetchFlightDetailsWithCircuitBreaker(Long flightId) {
//        return flightServiceClient.getFlightDetails(flightId);
//    }
   
   

    @CircuitBreaker(name = FLIGHT_CB, fallbackMethod = "flightDetailsFallback")
    public FlightDTO fetchFlightDetailsWithCircuitBreaker(Long flightId) {
        ApiResponse<FlightDTO> resp = flightServiceClient.getFlightDetails(flightId);

        if (resp == null) {
            log.error("Flight service returned null response for id {}", flightId);
            throw new FlightServiceException("Flight service returned no data for flight id " + flightId);
        }

        if (!resp.isSuccess()) {
            log.error("Flight service responded with error for id {}: {}", flightId, resp.getMessage());
            throw new FlightServiceException("Flight service error: " + resp.getMessage());
        }

        FlightDTO flightDetails = resp.getData();
        if (flightDetails == null) {
            log.error("Flight service returned wrapper with null data for id {}: {}", flightId, resp);
            throw new FlightServiceException("Flight details not available for flight id " + flightId);
        }

        // defensive check for availableSeats
        if (flightDetails.getAvailableSeats() == null) {
            log.error("FlightDTO.availableSeats is null for id {}: {}", flightId, flightDetails);
            throw new FlightServiceException("Flight availability information is not available for flight id " + flightId);
        }

        return flightDetails;
    }


    /**
     * Fallback invoked when flight service calls fail / circuit is open.
     * Must match original params + Throwable as last parameter.
     */
    public FlightDTO flightDetailsFallback(Long flightId, Throwable t) {
        log.error("Flight service fallback invoked for id {}: {}", flightId, t.toString());
        // Throw an application-specific exception so controller returns appropriate error (503, etc.)
        throw new FlightServiceException("Flight service is currently unavailable. Please try again later.");
        // Alternatively you could return a default FlightDTO instead of throwing.
    }

    private Passenger createPassenger(PassengerRequest request, Booking booking, Long flightId) {
        return Passenger.builder()
                .booking(booking)
                .flightId(flightId)
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .seatNumber(request.getSeatNumber())
                .mealType(request.getMealType())
                .build();
    }

    private String generatePNR() {
        Random random = new Random();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder pnr = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            pnr.append(chars.charAt(random.nextInt(chars.length())));
        }

        // Ensure uniqueness
        while (bookingRepository.findByPnr(pnr.toString()).isPresent()) {
            pnr = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                pnr.append(chars.charAt(random.nextInt(chars.length())));
            }
        }

        return pnr.toString();
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
        List<PassengerResponse> passengerResponses = booking.getPassengers().stream()
                .map(p -> PassengerResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .gender(p.getGender().toString())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .mealType(p.getMealType().toString())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .pnr(booking.getPnr())
                .flightId(booking.getFlightId())
                .userName(booking.getUserName())
                .userEmail(booking.getUserEmail())
                .numberOfSeats(booking.getNumberOfSeats())
                .bookingDateTime(booking.getBookingDateTime())
                .journeyDateTime(booking.getJourneyDateTime())
                .status(booking.getStatus().toString())
                .totalPrice(booking.getTotalPrice())
                .passengers(passengerResponses)
                .build();
    }
}
