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

  // 🔹 ADDED: empty field validation
  if (!this.email || !this.password) {
    this.error = 'Email and password are required';
    return; //stop execution
  }

  //basic email format check
  //regex-explanation for email check
  //start [one or more characters but not space or @] @ [one or more characters but not space or @] . [one or more characters but not space or @] end
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(this.email)) {
    this.error = 'Please enter a valid email address';
    return; //stop execution
  }

  //CLEAR previous errors before API call
  this.error = '';

  this.authService.login({
    email: this.email,
    password: this.password,
  }).subscribe({
    next: (res) => {
      this.authService.saveToken(res.token);
      // localStorage.setItem('userEmail',this.email);
      // console.log('login successful');
      // localStorage.setItem('role',res.role);
      this.authService.saveUser(res,this.email)
      console.log("is user ?",this.authService.isUser());
      console.log("is admin ?",this.authService.isAdmin());

      this.router.navigate(['/search-flights']);
    },
    error: () => {
      this.error = 'Invalid credentials';
    },
  });
}


}
