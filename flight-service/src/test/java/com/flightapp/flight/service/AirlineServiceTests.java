package com.flightapp.flight.service;

import com.flightapp.flight.dto.request.AddAirlineRequest;
import com.flightapp.flight.dto.response.AirlineResponse;
import com.flightapp.flight.entity.Airline;
import com.flightapp.flight.exception.ResourceNotFoundException;
import com.flightapp.flight.repository.AirlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTests {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineService airlineService;

    @Captor
    private ArgumentCaptor<Airline> airlineCaptor;

    @BeforeEach
    void setUp() {
        // MockitoExtension handles initialization
    }

    @Test
    void addAirline_success() {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setName("TestAir");
        req.setCode("TST");
        req.setLogoUrl("http://logo.tst/logo.png");

        // no existing airline with code
        when(airlineRepository.findByCode("TST")).thenReturn(Optional.empty());

        // simulate save assigning id
        when(airlineRepository.save(any(Airline.class))).thenAnswer(invocation -> {
            Airline a = invocation.getArgument(0);
            a.setId(77L);
            return a;
        });

        AirlineResponse resp = airlineService.addAirline(req);

        verify(airlineRepository, times(1)).findByCode("TST");
        verify(airlineRepository, times(1)).save(airlineCaptor.capture());

        Airline saved = airlineCaptor.getValue();
        assertEquals("TestAir", saved.getName());
        assertEquals("TST", saved.getCode());
        assertEquals("http://logo.tst/logo.png", saved.getLogoUrl());
        assertTrue(saved.getIsActive());

        assertEquals(77L, resp.getId());
        assertEquals("TestAir", resp.getName());
    }

    @Test
    void addAirline_duplicateCode_throws() {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setName("DuplicateAir");
        req.setCode("DUP");
        req.setLogoUrl("http://logo.dup/logo.png");

        Airline existing = Airline.builder()
                .id(5L)
                .name("Existing")
                .code("DUP")
                .logoUrl("http://logo.dup/ex.png")
                .isActive(true)
                .build();

        when(airlineRepository.findByCode("DUP")).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> airlineService.addAirline(req));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(airlineRepository, times(1)).findByCode("DUP");
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void getAirline_success() {
        Airline a = Airline.builder()
                .id(10L)
                .name("FlyHigh")
                .code("FLH")
                .logoUrl("http://logo.fly/high.png")
                .isActive(true)
                .build();

        when(airlineRepository.findById(10L)).thenReturn(Optional.of(a));

        AirlineResponse resp = airlineService.getAirline(10L);

        assertEquals(10L, resp.getId());
        assertEquals("FlyHigh", resp.getName());
        assertEquals("FLH", resp.getCode());
        assertEquals("http://logo.fly/high.png", resp.getLogoUrl());

        verify(airlineRepository, times(1)).findById(10L);
    }

    @Test
    void getAirline_notFound_throws() {
        when(airlineRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> airlineService.getAirline(999L));
        verify(airlineRepository, times(1)).findById(999L);
    }

    @Test
    void getAllAirlines_returnsMappedList() {
        Airline a1 = Airline.builder()
                .id(1L)
                .name("A1")
                .code("A1C")
                .logoUrl("u1")
                .isActive(true)
                .build();

        Airline a2 = Airline.builder()
                .id(2L)
                .name("A2")
                .code("A2C")
                .logoUrl("u2")
                .isActive(true)
                .build();

        when(airlineRepository.findAllActive()).thenReturn(Arrays.asList(a1, a2));

        List<AirlineResponse> list = airlineService.getAllAirlines();

        assertEquals(2, list.size());
        assertEquals("A1", list.get(0).getName());
        assertEquals("A2", list.get(1).getName());

        verify(airlineRepository, times(1)).findAllActive();
    }

    @Test
    void getAllAirlines_empty_returnsEmptyList() {
        when(airlineRepository.findAllActive()).thenReturn(Collections.emptyList());

        List<AirlineResponse> list = airlineService.getAllAirlines();

        assertTrue(list.isEmpty());
        verify(airlineRepository, times(1)).findAllActive();
    }
}
