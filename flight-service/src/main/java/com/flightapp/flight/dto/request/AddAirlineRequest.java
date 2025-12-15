package com.flightapp.flight.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAirlineRequest {

    @NotBlank(message = "Airline name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Airline code is required")
    @Size(min = 1, max = 10, message = "Code must be between 1 and 10 characters")
    private String code;

    private String logoUrl;
}
