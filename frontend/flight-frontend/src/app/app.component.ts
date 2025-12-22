import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ViewBookingComponent } from "./features/flights/view-booking-component/view-booking-component";
import { NavbarComponent } from './navbar/navbar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})

export class App {
  // protected readonly title = signal('flight-frontend');
  isLoggedIn() {
    return !!localStorage.getItem('token');
  }
}
