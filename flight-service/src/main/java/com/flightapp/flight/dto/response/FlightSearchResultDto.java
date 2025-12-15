package com.flightapp.flight.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchResultDto {
    private List<FlightResponse> outboundFlights;
    private List<FlightResponse> returnFlights;
    private String tripType;
}
