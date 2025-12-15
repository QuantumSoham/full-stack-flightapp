package com.flightapp.booking.controller;

import com.flightapp.booking.dto.request.BookingRequest;
import com.flightapp.booking.dto.response.*;
import com.flightapp.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
		RequestMethod.DELETE, RequestMethod.PUT })
public class BookingController {

	private final BookingService bookingService;

	//booking by flight id
	@PostMapping("/booking/{flightId}")
	public ResponseEntity<ApiResponse<BookingResponse>> bookFlight(@PathVariable Long flightId,
			@Valid @RequestBody BookingRequest request) {
		try {
			log.info("Received booking request for flight ID: {}", flightId);
			request.setFlightId(flightId);
			BookingResponse booking = bookingService.bookFlight(request);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(ApiResponse.success("Flight booked successfully. PNR: " + booking.getPnr(), booking));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
		} catch (Exception e) {
			log.error("Error booking flight", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("Error booking flight: " + e.getMessage()));
		}
	}

	//get bookinng by pnr
	@GetMapping("/ticket/{pnr}")
	public ResponseEntity<ApiResponse<BookingResponse>> getTicket(@PathVariable String pnr) {
		try {
			log.info("Fetching ticket for PNR: {}", pnr);
			BookingResponse booking = bookingService.getBookingByPNR(pnr);
			return ResponseEntity.ok(ApiResponse.success("Ticket fetched successfully", booking));
		} catch (Exception e) {
			log.error("Error fetching ticket", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
		}
	}

	//get all bookings on a specific email
	@GetMapping("/booking/history/{emailId}")
	public ResponseEntity<ApiResponse<List<BookingHistoryItemDto>>> getBookingHistory(@PathVariable String emailId) {
		try {
			log.info("Fetching booking history for email: {}", emailId);
			List<BookingHistoryItemDto> bookings = bookingService.getBookingHistory(emailId);
			return ResponseEntity.ok(ApiResponse.success("Booking history fetched successfully", bookings));
		} catch (Exception e) {
			log.error("Error fetching booking history", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("Error fetching booking history: " + e.getMessage()));
		}
	}

	@DeleteMapping("/booking/cancel/{pnr}")
	public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable String pnr) {
		try {
			log.info("Cancelling booking with PNR: {}", pnr);
			BookingResponse booking = bookingService.cancelBooking(pnr);
			return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
		} catch (Exception e) {
			log.error("Error cancelling booking", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
		}
	}
}
