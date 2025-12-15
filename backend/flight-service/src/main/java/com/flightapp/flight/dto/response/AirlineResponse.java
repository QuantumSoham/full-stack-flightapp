package com.flightapp.flight.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirlineResponse {
    private Long id;
    private String name;
    private String code;
    private String logoUrl;
}
