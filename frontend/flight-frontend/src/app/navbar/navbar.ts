import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class NavbarComponent {
  email = '';

  constructor(private router: Router,public authservice: AuthService) {
    this.email = localStorage.getItem('userEmail') || '';
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}

