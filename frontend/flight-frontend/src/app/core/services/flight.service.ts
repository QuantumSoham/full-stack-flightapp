import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
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
  constructor(private http: HttpClient,  private authService: AuthService) {}
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
      `${this.baseUrl}/api/v1.0/flight/booking/${payload.flightId}`,
      payload
    );
  }
  //todo get all bookings of a particular user
getBookings(): Observable<any> {
  const email = this.authService.getUserEmail();

  if (!email) {
    throw new Error('User not logged in');
  }

  return this.http.get(
    `${this.baseUrl}/api/v1.0/flight/booking/history/${email}`
  );
}
  // getBookings ():Observable<any>
  // {
  //     // {{base_url}}/api/v1.0/flight/booking/history/john.doe@gmail.com
  //     return this.http.get(
  //         `${this.baseUrl}/api/v1.0/flight/booking/history/john.doe@gmail.com`
  //     );
  // }

  cancelBooking(pnr :string) :Observable<any>
  {
    return this.http.delete(
          `${this.baseUrl}/api/v1.0/flight/booking/cancel/${pnr}`
      );
  }
}
