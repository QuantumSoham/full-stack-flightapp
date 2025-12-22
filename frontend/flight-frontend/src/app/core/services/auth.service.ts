import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Marks this class as injectable by Angular's DI system
// providedIn: 'root' means:
// One shared instance for the entire app
//No need to register it in any module
@Injectable({ providedIn: 'root' })
export class AuthService {

  // Base URL of the API Gateway
  // All auth-related requests go through this
  private baseUrl = 'http://localhost:8062';

  // HttpClient is Angular's wrapper around fetch/XHR
  // Used for all backend communication
  constructor(private http: HttpClient) {}

  
  getUserEmail(): string | null {
  return localStorage.getItem('userEmail');
}

  // Sends login credentials to backend
  // Backend validates user and returns JWT + role info
  login(data: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, data);
  }

  // Sends registration details to backend
  // Backend creates user and immediately returns a JWT
  register(data: {
    email: string;
    password: string;
    fullName: string;
    role: string;
  }): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register`, data);
  }

  // Stores JWT token in localStorage after successful login
  // This token is later picked up by the HTTP interceptor
  saveToken(token: string) {
    localStorage.setItem('token', token);
  }

  // Reads JWT token from localStorage
  // Used by guards and interceptors to check auth state
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Logs the user out
  // Clearing localStorage removes JWT and any auth state
  logout() {
    localStorage.clear();
  }
}
