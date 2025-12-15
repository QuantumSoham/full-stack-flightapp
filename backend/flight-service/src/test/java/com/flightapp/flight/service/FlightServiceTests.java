package com.flightapp.flight.service;

import com.flightapp.flight.dto.request.AddFlightInventoryRequest;
import com.flightapp.flight.dto.request.FlightSearchRequest;
import com.flightapp.flight.dto.response.FlightResponse;
import com.flightapp.flight.dto.response.FlightSearchResultDto;
import com.flightapp.flight.entity.Airline;
import com.flightapp.flight.entity.Flight;
import com.flightapp.flight.exception.InsufficientSeatsException;
import com.flightapp.flight.exception.InvalidFlightDataException;
import com.flightapp.flight.exception.ResourceNotFoundException;
import com.flightapp.flight.repository.AirlineRepository;
import com.flightapp.flight.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTests {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private FlightService flightService;

    @Captor
    private ArgumentCaptor<Flight> flightCaptor;

    private Airline sampleAirline;

    @BeforeEach
    void setUp() {
        sampleAirline = Airline.builder()
                .id(5L)
                .name("SampleAir")
                .code("SMP")
                .logoUrl("http://logo.sample/air.png")
                .isActive(true)
                .build();
    }

    @Test
    void addFlightInventory_success() {
        AddFlightInventoryRequest req = new AddFlightInventoryRequest();
        req.setAirlineId(5L);
        req.setFlightNumber("SMP-101");
        req.setFromPlace("BLR");
        req.setToPlace("DEL");
        req.setDepartureDateTime(LocalDateTime.now().plusDays(1));
        req.setArrivalDateTime(LocalDateTime.now().plusDays(1).plusHours(2));
        req.setPriceOneWay(new BigDecimal("150.00"));
        req.setPriceRoundTrip(new BigDecimal("280.00"));
        req.setTotalSeats(120);

        when(airlineRepository.findById(5L)).thenReturn(Optional.of(sampleAirline));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> {
            Flight f = invocation.getArgument(0);
            f.setId(99L);
            return f;
        });

        FlightResponse resp = flightService.addFlightInventory(req);

        verify(airlineRepository, times(1)).findById(5L);
        verify(flightRepository, times(1)).save(flightCaptor.capture());

        Flight saved = flightCaptor.getValue();
        assertEquals("SMP-101", saved.getFlightNumber());
        assertEquals("BLR", saved.getFromPlace());
        assertEquals("DEL", saved.getToPlace());
        assertEquals(120, saved.getTotalSeats());
        assertEquals(120, saved.getAvailableSeats());
        assertTrue(saved.getIsActive());

        assertEquals(99L, resp.getId());
        assertEquals("SMP-101", resp.getFlightNumber());
        assertNotNull(resp.getAirline());
        assertEquals("SampleAir", resp.getAirline().getName());
    }

    @Test
    void addFlightInventory_invalidDatetime_throws() {
        AddFlightInventoryRequest req = new AddFlightInventoryRequest();
        req.setAirlineId(5L);
        req.setFlightNumber("BAD-001");
        req.setFromPlace("A");
        req.setToPlace("B");
        req.setDepartureDateTime(LocalDateTime.now().plusDays(2));
        req.setArrivalDateTime(LocalDateTime.now().plusDays(1)); // arrival before departure
        req.setPriceOneWay(new BigDecimal("50"));
        req.setPriceRoundTrip(new BigDecimal("90"));
        req.setTotalSeats(50);

        assertThrows(InvalidFlightDataException.class, () -> flightService.addFlightInventory(req));

        verify(airlineRepository, never()).findById(anyLong());
        verify(flightRepository, never()).save(any());
    }

    @Test
    void getFlight_success() {
        Flight flight = Flight.builder()
                .id(11L)
                .airline(sampleAirline)
                .flightNumber("SMP-202")
                .fromPlace("MUM")
                .toPlace("CCU")
                .departureDateTime(LocalDateTime.now().plusDays(3))
                .arrivalDateTime(LocalDateTime.now().plusDays(3).plusHours(2))
                .priceOneWay(new BigDecimal("120"))
                .priceRoundTrip(new BigDecimal("220"))
                .totalSeats(100)
                .availableSeats(100)
                .isActive(true)
                .build();

        when(flightRepository.findByIdAndIsActiveTrue(11L)).thenReturn(Optional.of(flight));

        FlightResponse resp = flightService.getFlight(11L);

        assertEquals(11L, resp.getId());
        assertEquals("SMP-202", resp.getFlightNumber());
        assertEquals(100, resp.getAvailableSeats());
        assertEquals("SampleAir", resp.getAirline().getName());
        verify(flightRepository, times(1)).findByIdAndIsActiveTrue(11L);
    }

    @Test
    void getFlight_notFound_throws() {
        when(flightRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> flightService.getFlight(999L));
        verify(flightRepository, times(1)).findByIdAndIsActiveTrue(999L);
    }

//    @Test
//    void searchFlights_oneWay_filtersByAvailableSeats_and_returnsRoundTrip_if_requested() {
//        // Prepare outbound flights (one has enough seats, one doesn't)
//        Flight f1 = Flight.builder()
//                .id(1L)
//                .airline(sampleAirline)
//                .flightNumber("O1")
//                .availableSeats(5)
//                .departureDateTime(LocalDateTime.now().plusDays(2))
//                .arrivalDateTime(LocalDateTime.now().plusDays(2).plusHours(2))
//                .totalSeats(10)
//                .priceOneWay(new BigDecimal("50"))
//                .isActive(true)
//                .build();
//
//        Flight f2 = Flight.builder()
//                .id(2L)
//                .airline(sampleAirline)
//                .flightNumber("O2")
//                .availableSeats(1)
//                .departureDateTime(LocalDateTime.now().plusDays(2))
//                .arrivalDateTime(LocalDateTime.now().plusDays(2).plusHours(3))
//                .totalSeats(10)
//                .priceOneWay(new BigDecimal("80"))
//                .isActive(true)
//                .build();
//
//        // return flights
//        Flight r1 = Flight.builder()
//                .id(3L)
//                .airline(sampleAirline)
//                .flightNumber("R1")
//                .availableSeats(4)
//                .departureDateTime(LocalDateTime.now().plusDays(5))
//                .arrivalDateTime(LocalDateTime.now().plusDays(5).plusHours(2))
//                .totalSeats(10)
//                .priceOneWay(new BigDecimal("60"))
//                .isActive(true)
//                .build();
//
//        when(flightRepository.searchOneWayFlights("BLR", "DEL", LocalDateTime.now().plusDays(2)))
//                .thenReturn(Arrays.asList(f1, f2));
//
//        when(flightRepository.searchRoundTripReturn("DEL", "BLR", LocalDateTime.now().plusDays(5)))
//                .thenReturn(Collections.singletonList(r1));
//
//        FlightSearchRequest req = new FlightSearchRequest();
//        req.setFromPlace("BLR");
//        req.setToPlace("DEL");
//        req.setDepartureDate(LocalDateTime.now().plusDays(2));
//        req.setReturnDate(LocalDateTime.now().plusDays(5));
//        req.setTripType("ROUND_TRIP");
//        req.setNumberOfSeats(2);
//
//        FlightSearchResultDto result = flightService.searchFlights(req);
//
//        // Only f1 should be returned (f2 has only 1 available seat)
//        assertEquals(1, result.getOutboundFlights().size());
//        assertEquals("O1", result.getOutboundFlights().get(0).getFlightNumber());
//
//        // Return flights: r1 has 4 seats so it should be included
//        assertNotNull(result.getReturnFlights());
//        assertEquals(1, result.getReturnFlights().size());
//        assertEquals("R1", result.getReturnFlights().get(0).getFlightNumber());
//    }

    @Test
    void reduceAvailableSeats_success() {
        Flight flight = Flight.builder()
                .id(21L)
                .airline(sampleAirline)
                .availableSeats(10)
                .totalSeats(10)
                .isActive(true)
                .build();

        when(flightRepository.findById(21L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        flightService.reduceAvailableSeats(21L, 4);

        verify(flightRepository, times(1)).findById(21L);
        verify(flightRepository, times(1)).save(flightCaptor.capture());

        Flight saved = flightCaptor.getValue();
        assertEquals(6, saved.getAvailableSeats());
    }

    @Test
    void reduceAvailableSeats_insufficient_throws() {
        Flight flight = Flight.builder()
                .id(22L)
                .airline(sampleAirline)
                .availableSeats(2)
                .totalSeats(100)
                .isActive(true)
                .build();

        when(flightRepository.findById(22L)).thenReturn(Optional.of(flight));

        assertThrows(InsufficientSeatsException.class, () -> flightService.reduceAvailableSeats(22L, 5));

        verify(flightRepository, times(1)).findById(22L);
        verify(flightRepository, never()).save(any());
    }

    @Test
    void increaseAvailableSeats_success() {
        Flight flight = Flight.builder()
                .id(31L)
                .airline(sampleAirline)
                .availableSeats(3)
                .totalSeats(10)
                .isActive(true)
                .build();

        when(flightRepository.findById(31L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        flightService.increaseAvailableSeats(31L, 5);

        verify(flightRepository, times(1)).findById(31L);
        verify(flightRepository, times(1)).save(flightCaptor.capture());

        Flight saved = flightCaptor.getValue();
        assertEquals(8, saved.getAvailableSeats());
    }
}
