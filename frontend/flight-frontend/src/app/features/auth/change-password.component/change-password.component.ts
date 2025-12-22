import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css',
})
export class ChangePasswordComponent {

  email = '';
  oldPassword = '';
  newPassword = '';

  error = '';
  success = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  changePassword() {
    this.error = '';
    this.success = '';

    if (!this.email || !this.oldPassword || !this.newPassword) {
      this.error = 'All fields are required';
      return;
    }

    this.authService.changePassword({
      email: this.email,
      oldPassword: this.oldPassword,
      newPassword: this.newPassword,
    }).subscribe({
      next: () => {
        this.success = 'Password updated successfully';

        // optional redirect after success
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: () => {
        this.error = 'Failed to update password';
      }
    });
  }
}
