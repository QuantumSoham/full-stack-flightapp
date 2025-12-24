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
  // seat map
  seats: string[] = [];
  seatsByRow: string[][] = [];
  selectedSeats: string[] = [];
  constructor(private flightService: FlightService, private router: Router, private authservice: AuthService) {}

  // generate 30 rows x 6 cols (A-F) => 180 seats
  private generateSeats() {
    const cols = ['A','B','C','D','E','F'];
    for (let r = 1; r <= 30; r++) {
      const rowSeats: string[] = [];
      for (let c of cols) {
        rowSeats.push(`${r}${c}`);
      }
      this.seatsByRow.push(rowSeats);
    }
    // flat list for any legacy usage
    this.seats = this.seatsByRow.flat();
  }

  // initialize seats
  ngOnInit() {
    this.generateSeats();
    this.syncSelectedToPassengers();
  }

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
    // ensure selected seats array matches number of seats requested
    if (this.selectedSeats.length > count) {
      this.selectedSeats = this.selectedSeats.slice(0, count);
    }
    this.syncSelectedToPassengers();
  }

  // toggle seat selection (simple beginner-friendly logic)
  toggleSeat(seat: string) {
    const i = this.selectedSeats.indexOf(seat);
    const max = this.booking.numberOfSeats || 1;
    if (i >= 0) {
      this.selectedSeats.splice(i,1);
    } else {
      if (this.selectedSeats.length < max) {
        this.selectedSeats.push(seat);
      } else {
        // if already at max, replace the oldest selection
        this.selectedSeats.shift();
        this.selectedSeats.push(seat);
      }
    }
    this.syncSelectedToPassengers();
  }

  // assign selected seats to passenger objects in order
  private syncSelectedToPassengers() {
    for (let i = 0; i < this.booking.passengers.length; i++) {
      this.booking.passengers[i].seatNumber = this.selectedSeats[i] || '';
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
          this.router.navigate(['/view-booking']);
        },
        error:()=>{
          this.error='Booking not successful'
        }
      }
    )
      
  }
}
