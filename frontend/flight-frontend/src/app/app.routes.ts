import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { SearchFlightsComponent } from './features/flights/search-flights/search-flights.component';
import { BookComponent } from './features/flights/book/book';
import { ViewBookingComponent } from './features/flights/view-booking-component/view-booking-component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'search-flights', component: SearchFlightsComponent },
  {path:'book-flights/:id',component: BookComponent},
  {path:'view-booking',component: ViewBookingComponent},
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  
];
