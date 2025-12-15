import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  // constructor(private authService: AuthService) {}

  // login() {
  //   this.authService.login({
  //     email: this.email,
  //     password: this.password,
  //   }).subscribe({
  //     next: (res) => {
  //       this.authService.saveToken(res.token);
  //       alert(`Logged in as ${res.role}`);
  //     },
  //     error: () => {
  //       this.error = 'Invalid credentials';
  //     },
  //   });
  // }
  constructor(
  private authService: AuthService,
  private router: Router
) {}

login() {
  this.authService.login({
    email: this.email,
    password: this.password,
  }).subscribe({
    next: (res) => {
      this.authService.saveToken(res.token);
      console.log("login successful");
      this.router.navigate(['/search-flights']);
    },
    error: () => {
      this.error = 'Invalid credentials';
    },
  });
}

}
