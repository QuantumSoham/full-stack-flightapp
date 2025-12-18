import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Marks this class as a service Angular can manage
// providedIn: 'root' means there will be ONE shared instance
// Any component that asks for FlightService gets the same one
@Injectable({ providedIn: 'root' })
export class FlightService {

  // Base URL of the API Gateway
  // All flight-related backend calls go through this
  private baseUrl = 'http://localhost:8062';

  // HttpClient is Angular’s built-in way to make HTTP requests
  // Angular injects it automatically
  constructor(private http: HttpClient) {}

  // Sends a flight search request to the backend
  // payload contains fromPlace, toPlace, date, seats, tripType, etc.
  // Backend responds with a list of matching flights
  searchFlights(payload: any): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/api/v1.0/flight/search`,
      payload
    );

  }
  bookFlight(payload: any): Observable<any>{
    return this.http.post(
      `${this.baseUrl}/api/v1.0/flight/booking/1`,
      payload
    )
  }
}
