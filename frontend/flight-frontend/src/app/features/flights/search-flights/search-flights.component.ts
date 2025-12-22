import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlightService } from '../../../core/services/flight.service';
import { Router } from '@angular/router';
import { ViewBookingComponent } from '../view-booking-component/view-booking-component';

@Component({
  selector: 'app-search-flights',
  standalone: true,
  imports: [CommonModule, FormsModule, ViewBookingComponent],
  templateUrl: './search-flights.component.html',
  styleUrl: './search-flights.component.css',
})
export class SearchFlightsComponent {
  fromPlace = 'Delhi';
  toPlace = 'Calcutta';
  departureDate = '';
  numberOfSeats = 1;
  tripType = 'ONE_WAY';

  flights: any[] = [];
  error = '';

  
  constructor(private flightService: FlightService,
    private router: Router
  ) {}

  search() {
    const payload = {
      fromPlace: this.fromPlace,
      toPlace: this.toPlace,
      departureDate: this.departureDate+':00',
      numberOfSeats: this.numberOfSeats,
      tripType: this.tripType,
    };
    console.log(this.departureDate);
    if (!this.departureDate || !this.toPlace || !this.departureDate) {
    this.error = 'Please enter all the fields!';
    return; //stop execution
  }
    this.flightService.searchFlights(payload).subscribe({
      next: (response) => {
        this.flights = response.data.outboundFlights || [];
      },
      error: () => {
        this.error = 'No flights found!';
      },
    });
  }
  //todo book in the future
  book(id:number)
  {
    this.router.navigate(['/book-flights',id])
  }
}
