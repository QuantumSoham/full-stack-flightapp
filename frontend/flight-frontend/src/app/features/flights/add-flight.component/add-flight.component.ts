import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FlightService } from '../../../core/services/flight.service';

@Component({
  selector: 'app-add-flight.component',
  standalone:true,
  imports: [CommonModule,FormsModule],
  templateUrl: './add-flight.component.html',
  styleUrl: './add-flight.component.css',
})
export class AddFlightComponent {

flight = {
    airlineId: 1,
    flightNumber: '',
    fromPlace: '',
    toPlace: '',
    departureDateTime: '',
    arrivalDateTime: '',
    totalSeats: 0,
    economySeats: 0,
    businessSeats: 0,
    priceOneWay: 0,
  };

   error = '';
  success = '';

  constructor(private flightservice: FlightService) {}
    reset() {
    this.flight = {
      airlineId: 1,
      flightNumber: '',
      fromPlace: '',
      toPlace: '',
      departureDateTime: '',
      arrivalDateTime: '',
      totalSeats: 0,
      economySeats: 0,
      businessSeats: 0,
      priceOneWay: 0,
    };
  }
submit() {
    this.error = '';
    this.success = '';

    if (this.flight.economySeats + this.flight.businessSeats !== this.flight.totalSeats) {
      this.error = 'Economy + Business seats must equal Total seats';
      return;
    }

    if (this.flight.departureDateTime >= this.flight.arrivalDateTime) {
      this.error = 'Arrival time must be after departure time';
      return;
    }

    this.flightservice.addFlight(this.flight).subscribe({
      next: () => {
        this.success = 'Flight added successfully';
        this.reset();
      },
      error: () => {
        this.error = 'Failed to add flight';
      }
    });

}
}
