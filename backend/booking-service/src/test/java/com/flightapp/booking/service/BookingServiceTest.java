package com.flightapp.booking.service;

import com.flightapp.booking.client.FlightServiceClient;
import com.flightapp.booking.dto.request.BookingRequest;
import com.flightapp.booking.dto.request.PassengerRequest;
import com.flightapp.booking.dto.response.ApiResponse;
import com.flightapp.booking.dto.response.BookingHistoryItemDto;
//import com.flightapp.booking.dto.response.BookingMessageDto;
import com.flightapp.booking.dto.response.FlightDTO;
import com.flightapp.booking.dto.response.PassengerResponse;
import com.flightapp.booking.dto.response.BookingResponse;
import com.flightapp.booking.entity.Booking;
import com.flightapp.booking.entity.Passenger;
import com.flightapp.booking.entity.UserAccount;
import com.flightapp.booking.exception.BookingCancellationException;
import com.flightapp.booking.exception.FlightServiceException;
import com.flightapp.booking.exception.ResourceNotFoundException;
import com.flightapp.booking.repository.BookingRepository;
import com.flightapp.booking.repository.PassengerRepository;
import com.flightapp.booking.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private FlightServiceClient flightServiceClient;

    @InjectMocks
    private BookingService bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;

    @BeforeEach
    void setUp() {
        // no-op (Mockito annotations handle initialization)
    }

    @Test
    void bookFlight_success() {
        // Prepare flight dto wrapped in ApiResponse
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setAvailableSeats(10);
        flightDTO.setPriceOneWay(new BigDecimal("100.00"));

        ApiResponse<FlightDTO> apiResp = new ApiResponse<>();
        apiResp.setSuccess(true);
        apiResp.setData(flightDTO);

        when(flightServiceClient.getFlightDetails(1L)).thenReturn(apiResp);

        // No existing user
        when(userAccountRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        // bookingRepository.save should return the saved booking with id
        // We capture the booking passed to save and return same with an id
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(123L);
            return b;
        });

        // passengerRepository.saveAll should return the saved list (simulate DB assigning ids)
        when(passengerRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Passenger> list = invocation.getArgument(0);
            long id = 1;
            for (Passenger p : list) p.setId(id++);
            return list;
        });

        // Build booking request with 2 passengers
        PassengerRequest p1 = new PassengerRequest();
        p1.setName("John");
        p1.setAge(30);
        p1.setGender(Passenger.Gender.MALE);
        p1.setSeatNumber("1A");
        p1.setMealType(Passenger.MealType.VEG);

        PassengerRequest p2 = new PassengerRequest();
        p2.setName("Jane");
        p2.setAge(28);
        p2.setGender(Passenger.Gender.FEMALE);
        p2.setSeatNumber("1B");
        p2.setMealType(Passenger.MealType.NON_VEG);

        BookingRequest req = new BookingRequest();
        req.setFlightId(1L);
        req.setNumberOfSeats(2);
        req.setUserEmail("alice@example.com");
        req.setUserName("Alice");
        req.setPassengers(Arrays.asList(p1, p2));

        // call
        BookingResponse resp = bookingService.bookFlight(req);

        // verify interactions
        verify(flightServiceClient, times(1)).getFlightDetails(1L);
        verify(bookingRepository, times(1)).save(bookingCaptor.capture());
        verify(passengerRepository, times(1)).saveAll(anyList());
        verify(flightServiceClient, times(1)).reduceFlightSeats(1L, 2);

        Booking savedBooking = bookingCaptor.getValue();
        assertNotNull(savedBooking.getPnr());
        assertEquals("Alice", savedBooking.getUserName());
        assertEquals(2, resp.getNumberOfSeats());
        assertEquals(new BigDecimal("200.00"), resp.getTotalPrice());
        assertEquals(2, resp.getPassengers().size());
        assertEquals(123L, resp.getId());
    }

    @Test
    void bookFlight_insufficientSeats_throws() {
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setAvailableSeats(1); // less than requested
        flightDTO.setPriceOneWay(new BigDecimal("100.00"));

        ApiResponse<FlightDTO> apiResp = new ApiResponse<>();
        apiResp.setSuccess(true);
        apiResp.setData(flightDTO);

        when(flightServiceClient.getFlightDetails(2L)).thenReturn(apiResp);

        BookingRequest req = new BookingRequest();
        req.setFlightId(2L);
        req.setNumberOfSeats(2);
        req.setUserEmail("bob@example.com");
        req.setUserName("Bob");
        req.setPassengers(Collections.emptyList());

        assertThrows(FlightServiceException.class, () -> bookingService.bookFlight(req));

        verify(bookingRepository, never()).save(any());
        verify(passengerRepository, never()).saveAll(anyList());
    }

//    @Test
//    void cancelBooking_success() {
//        // prepare an existing booking
//        Booking booking = Booking.builder()
//                .id(11L)
//                .pnr("PNR123")
//                .flightId(5L)
//                .userEmail("carl@example.com")
//                .numberOfSeats(3)
//                .status(Booking.Status.BOOKED)
//                .journeyDateTime(LocalDateTime.now().plusDays(5))
//                .build();
//
//        when(bookingRepository.findByPnr("PNR123")).thenReturn(Optional.of(booking));
//        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        BookingResponse resp = bookingService.cancelBooking("PNR123");
//
//        assertEquals("CANCELLED", resp.getStatus());
//        verify(flightServiceClient, times(1)).increaseFlightSeats(5L, 3);
//    }

    @Test
    void cancelBooking_tooLate_throws() {
        Booking booking = Booking.builder()
                .id(12L)
                .pnr("PNR999")
                .flightId(6L)
                .userEmail("dave@example.com")
                .numberOfSeats(1)
                .status(Booking.Status.BOOKED)
                .journeyDateTime(LocalDateTime.now().plusHours(10)) // less than 24 hours
                .build();

        when(bookingRepository.findByPnr("PNR999")).thenReturn(Optional.of(booking));

        assertThrows(BookingCancellationException.class, () -> bookingService.cancelBooking("PNR999"));

        verify(bookingRepository, never()).save(any());
        verify(flightServiceClient, never()).increaseFlightSeats(anyLong(), anyInt());
    }

    @Test
    void getBookingByPNR_notFound_throws() {
        when(bookingRepository.findByPnr("NOPE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingByPNR("NOPE"));
    }

    @Test
    void getBookingHistory_returnsMappedList() {
        Booking b1 = Booking.builder()
                .id(1L)
                .pnr("A1")
                .flightId(10L)
                .bookingDateTime(LocalDateTime.now().minusDays(2))
                .journeyDateTime(LocalDateTime.now().plusDays(3))
                .status(Booking.Status.BOOKED)
                .numberOfSeats(1)
                .totalPrice(new BigDecimal("50"))
                .userEmail("z@example.com")
                .build();

        Booking b2 = Booking.builder()
                .id(2L)
                .pnr("A2")
                .flightId(11L)
                .bookingDateTime(LocalDateTime.now().minusDays(1))
                .journeyDateTime(LocalDateTime.now().plusDays(4))
                .status(Booking.Status.CANCELLED)
                .numberOfSeats(2)
                .totalPrice(new BigDecimal("120"))
                .userEmail("z@example.com")
                .build();

        when(bookingRepository.findByUserEmailOrderByBookingDateTimeDesc("z@example.com"))
                .thenReturn(Arrays.asList(b2, b1));

        List<BookingHistoryItemDto> history = bookingService.getBookingHistory("z@example.com");

        assertEquals(2, history.size());
        assertEquals("A2", history.get(0).getPnr()); // order preserved
        assertEquals("A1", history.get(1).getPnr());
    }
}
