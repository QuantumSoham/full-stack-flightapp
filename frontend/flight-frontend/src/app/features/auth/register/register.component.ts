import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  email = '';
  password = '';
  fullName = '';
  role = 'ROLE_USER';
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  register() {
    this.authService.register({
      email: this.email,
      password: this.password,
      fullName: this.fullName,
      role: this.role,
    }).subscribe({
      next: (res) => {
        this.authService.saveToken(res.token);
        alert('Registration successful');
        
        this.router.navigate(['/login']);
      },
      error: () => {
        this.error = 'Registration failed';
      },
    });
  }
}
