import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlightService } from '../../../core/services/flight.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-view-booking-component',
  standalone:true,
  imports: [FormsModule, DatePipe,CommonModule],
  templateUrl: './view-booking-component.html',
  styleUrl: './view-booking-component.css',
})
export class ViewBookingComponent implements OnInit
{
  ngOnInit() {
    this.showBookings();
  }
    bookings: any[]=[];
    email='';
    error='';
    cancelmsg='';
    constructor(private flightservice:FlightService,  private router: Router)
    {}

    showBookings()
    {
      this.flightservice.getBookings().subscribe({
        next: (response)=>{
          this.bookings=response.data || [];
        },
        error:
        ()=>{
          console.log(this.bookings);
          this.error ='No booking found';
        }
      })
    }
    cancelBooking(pnr :string) 
    {
        this.flightservice.cancelBooking(pnr).subscribe({
        next: (response)=>{
          this.cancelBooking=response.message || '';
          console.log(this.cancelBooking);
        },
        error:
        ()=>{
          
          this.error ='Couldnt Cancel booking';
          alert(this.error);
        }
      })

      this.showBookings();
    }

}
