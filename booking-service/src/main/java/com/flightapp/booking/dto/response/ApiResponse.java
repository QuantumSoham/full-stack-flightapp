package com.flightapp.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//a custom class to wrap my response with other meta data to know if server is working fines
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	private boolean success; // request success message
	private String message;// message of request
	private T data;// actual data
	private String timestamp;// timestamp

	public static <T> ApiResponse<T> success(String message, T data) {
		return ApiResponse.<T>builder().success(true).message(message).data(data)
				.timestamp(java.time.LocalDateTime.now().toString()).build();
	}

	public static <T> ApiResponse<T> error(String message) {
		return ApiResponse.<T>builder().success(false).message(message)
				.timestamp(java.time.LocalDateTime.now().toString()).build();
	}
}
