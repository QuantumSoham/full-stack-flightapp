import { Component, Input } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FlightService } from '../../../core/services/flight.service';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';


@Component({
  standalone:true,
  selector: 'app-book',
  imports: [FormsModule, RouterModule, CommonModule],
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
      this.createPassenger()
    ],
  };
  @Input() id:string='';
  error = '';
  constructor(private flightService: FlightService, private router: Router, private authservice: AuthService) {}

  createPassenger(){
    return {
        name: "John Doe",
        age: 30,
        gender: "MALE",
        seatNumber: "14A",
        mealType: "VEG"
      };
  }
  updatePassengers()
  {
    const count=this.booking.numberOfSeats;
    while(this.booking.passengers.length < count)
    {
      this.booking.passengers.push(this.createPassenger()); 
    }

    while(this.booking.passengers.length > count)
    {
      this.booking.passengers.pop();
    }
  }
  bookFlight() {
     const email = this.authservice.getUserEmail();

  if (!email) {
    this.error = 'User not logged in';
    this.router.navigate(['/login']);
    return;
  }

  this.booking.userEmail = email;
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
