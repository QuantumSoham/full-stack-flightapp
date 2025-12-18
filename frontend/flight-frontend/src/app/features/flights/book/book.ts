import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FlightService } from '../../../core/services/flight.service';

@Component({
  selector: 'app-book',
  imports: [FormsModule, RouterModule],
  templateUrl: './book.html',
  styleUrl: './book.css',
})
export class BookComponent {
  booking = {
    flightId: 1,
    userName: 'John Doe',
    userEmail: 'john.doe@gmail.com',
    numberOfSeats: 1,
    passengers: [
      {
        name: "John Doe",
        age: 30,
        gender: "MALE",
        seatNumber: "14A",
        mealType: "VEG"
      }
    ],
  };
  error = '';
  constructor(private flightService: FlightService, private router: Router) {}

  bookFlight() {
    this.flightService.bookFlight(this.booking).subscribe(
      {
        next:(response)=>{
          console.log(response);
        },
        error:()=>{
          this.error='Booking not successful'
        }
      }
    )
      
  }
}
