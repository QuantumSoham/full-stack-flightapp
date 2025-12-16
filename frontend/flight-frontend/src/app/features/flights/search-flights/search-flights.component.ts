import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlightService } from '../../../core/services/flight.service';

@Component({
  selector: 'app-search-flights',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-flights.component.html',
  styleUrl: './search-flights.component.css',
})
export class SearchFlightsComponent {
  fromPlace = '';
  toPlace = '';
  departureDate = '';
  numberOfSeats = 1;
  tripType = 'ONE_WAY';

  flights: any[] = [];
  error = '';

  constructor(private flightService: FlightService) {}

  search() {
    const payload = {
      fromPlace: this.fromPlace,
      toPlace: this.toPlace,
      departureDate: this.departureDate+':00',
      numberOfSeats: this.numberOfSeats,
      tripType: this.tripType,
    };
    console.log(this.departureDate);
    this.flightService.searchFlights(payload).subscribe({
      next: (res) => {
        this.flights = res.data.outboundFlights || [];
      },
      error: () => {
        this.error = 'No flights found or unauthorized';
      },
    });
  }
  //todo book in the future
  book()
  {
    alert("Flight Selected!");
  }
}
