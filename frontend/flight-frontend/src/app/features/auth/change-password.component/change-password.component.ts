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
  passwordErrors: string[] = [];
  passwordRules = {
    minLength: false,
    hasUpper: false,
    hasLower: false,
    hasNumber: false,
    hasSpecial: false,
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // changePassword() {
  //   this.error = '';
  //   this.success = '';

  //   if (!this.email || !this.oldPassword || !this.newPassword) {
  //     this.error = 'All fields are required';
  //     return;
  //   }

  //   this.authService.changePassword({
  //     email: this.email,
  //     oldPassword: this.oldPassword,
  //     newPassword: this.newPassword,
  //   }).subscribe({
  //     next: () => {
  //       this.success = 'Password updated successfully';

  //       // optional redirect after success
  //       setTimeout(() => {
  //         this.router.navigate(['/login']);
  //       }, 1500);
  //     },
  //     error: () => {
  //       this.error = 'Failed to update password';
  //     }
  //   });
  // }
  passwordRegex =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

validatePasswordRules() {
  const p = this.newPassword || '';
  this.passwordRules.minLength = p.length >= 8;
  this.passwordRules.hasUpper = /[A-Z]/.test(p);
  this.passwordRules.hasLower = /[a-z]/.test(p);
  this.passwordRules.hasNumber = /\d/.test(p);
  this.passwordRules.hasSpecial = /[@$!%*?&]/.test(p);

  this.passwordErrors = [];
  if (!this.passwordRules.minLength) this.passwordErrors.push('At least 8 characters');
  if (!this.passwordRules.hasUpper) this.passwordErrors.push('At least one uppercase letter');
  if (!this.passwordRules.hasLower) this.passwordErrors.push('At least one lowercase letter');
  if (!this.passwordRules.hasNumber) this.passwordErrors.push('At least one number');
  if (!this.passwordRules.hasSpecial) this.passwordErrors.push('At least one special character (e.g. @ $ ! % *)');
}

isPasswordValid(): boolean {
  this.validatePasswordRules();
  return Object.values(this.passwordRules).every(Boolean);
}
changePassword() {
  this.error = '';
  this.success = '';

  if (!this.email || !this.oldPassword || !this.newPassword) {
    this.error = 'All fields are required';
    return;
  }

  if (!this.isPasswordValid()) {
    // build a friendly error summary from the rule checks
    this.validatePasswordRules();
    if (this.passwordErrors.length) {
      this.error = 'Please fix the following password requirements:';
      return;
    }
  }

  this.authService.changePassword({
    email: this.email,
    oldPassword: this.oldPassword,
    newPassword: this.newPassword,
  }).subscribe({
    next: () => {
      this.success = 'Password updated successfully';
      setTimeout(() => this.router.navigate(['/login']), 1500);
    },
    error: () => {
      this.error = 'Failed to update password';
    },
  });
}

}
