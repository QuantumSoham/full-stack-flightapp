package com.flightapp.booking.client;

import com.flightapp.booking.dto.response.ApiResponse;
import com.flightapp.booking.dto.response.FlightDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// 'flight-service' must match the flight service spring.application.name
@FeignClient(name = "flight-service")
public interface FlightServiceClient {

	@GetMapping("/api/v1.0/flight/{flightId}")
	ApiResponse<FlightDTO> getFlightDetails(@PathVariable("flightId") Long flightId);

	// Mapping Flight service api to booking service through http calls
	@PostMapping("/api/v1.0/flight/{flightId}/reduceSeats")
	void reduceFlightSeats(@PathVariable("flightId") Long flightId, @RequestParam("count") Integer count);

	@PostMapping("/api/v1.0/flight/{flightId}/increaseSeats")
	void increaseFlightSeats(@PathVariable("flightId") Long flightId, @RequestParam("count") Integer count);
}

//Implementation I experimented with Rest Template , very verbose 
//package com.flightapp.booking.client;
//
//import com.flightapp.booking.dto.response.FlightDTO;
//import com.flightapp.booking.exception.FlightServiceException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Map;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class FlightServiceClient {
//
//    private final RestTemplate restTemplate;
//    private final DiscoveryClient discoveryClient;
//
//    private static final String FLIGHT_SERVICE_NAME = "flight-service";
//
//    /**
//     * Get flight details by flight ID
//     */
//    public FlightDTO getFlightDetails(Long flightId) {
//        try {
//            log.info("Fetching flight details for flight ID: {}", flightId);
//            String url = getFlightServiceBaseUrl() + "/api/v1.0/flight/" + flightId;
//
//            var response = restTemplate.getForObject(url, Map.class);
//            if (response != null && (Boolean) response.get("success")) {
//                Map<String, Object> flightData = (Map<String, Object>) response.get("data");
//                return convertToFlightDTO(flightData);
//            }
//            throw new FlightServiceException("Failed to fetch flight details");
//        } catch (Exception e) {
//            log.error("Error fetching flight details for flight ID: {}", flightId, e);
//            throw new FlightServiceException("Error communicating with Flight Service", e);
//        }
//    }
//
//    /**
//     * Reduce available seats for a flight
//     */
//    public void reduceFlightSeats(Long flightId, Integer seatCount) {
//        try {
//            log.info("Reducing seats for flight ID: {} by count: {}", flightId, seatCount);
//            String url = getFlightServiceBaseUrl() + "/api/v1.0/flight/" + flightId + "/reduce-seats?count=" + seatCount;
//
//            restTemplate.put(url, null);
//            log.info("Seats reduced successfully");
//        } catch (Exception e) {
//            log.error("Error reducing seats for flight ID: {}", flightId, e);
//            throw new FlightServiceException("Error communicating with Flight Service", e);
//        }
//    }
//
//    /**
//     * Increase available seats for a flight (for cancellations)
//     */
//    public void increaseFlightSeats(Long flightId, Integer seatCount) {
//        try {
//            log.info("Increasing seats for flight ID: {} by count: {}", flightId, seatCount);
//            String url = getFlightServiceBaseUrl() + "/api/v1.0/flight/" + flightId + "/increase-seats?count=" + seatCount;
//
//            restTemplate.put(url, null);
//            log.info("Seats increased successfully");
//        } catch (Exception e) {
//            log.error("Error increasing seats for flight ID: {}", flightId, e);
//            throw new FlightServiceException("Error communicating with Flight Service", e);
//        }
//    }
//
//    /**
//     * Get base URL for flight service using Eureka discovery
//     */
//    private String getFlightServiceBaseUrl() {
//        try {
//            List<ServiceInstance> instances = discoveryClient.getInstances(FLIGHT_SERVICE_NAME);
//
//            if (!instances.isEmpty()) {
//                ServiceInstance instance = instances.get(0);
//                String url = instance.getUri().toString();
//                log.info("Using Flight Service URL from Eureka: {}", url);
//                return url;
//            }
//        } catch (Exception e) {
//            log.warn("Error fetching Flight Service from Eureka", e);
//        }
//
//        // Fallback URL
//        log.warn("Using fallback Flight Service URL");
//        return "http://localhost:9001";
//    }
//
//    private FlightDTO convertToFlightDTO(Map<String, Object> data) {
//        FlightDTO dto = new FlightDTO();
//        dto.setId(((Number) data.get("id")).longValue());
//        dto.setFlightNumber((String) data.get("flightNumber"));
//        dto.setFromPlace((String) data.get("fromPlace"));
//        dto.setToPlace((String) data.get("toPlace"));
//        dto.setAvailableSeats(((Number) data.get("availableSeats")).intValue());
//
//        if (data.get("priceOneWay") != null) {
//            dto.setPriceOneWay(new java.math.BigDecimal(data.get("priceOneWay").toString()));
//        }
//        if (data.get("priceRoundTrip") != null) {
//            dto.setPriceRoundTrip(new java.math.BigDecimal(data.get("priceRoundTrip").toString()));
//        }
//
//        return dto;
//    }
//}
