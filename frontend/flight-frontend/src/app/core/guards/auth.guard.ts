// import { CanActivateFn } from '@angular/router';

// export const authGuard: CanActivateFn = (route, state) => {
//   return true;
// };
// Angular utility to lazily fetch dependencies
// Used instead of constructor injection inside functional guards
import { inject } from '@angular/core';

// CanActivateFn = function that decides whether a route can be opened
// Router is used to redirect the user if access is denied
import { CanActivateFn, Router } from '@angular/router';

// AuthService holds login / logout logic and JWT token access
import { AuthService } from '../services/auth.service';


// This is a route guard function
// It runs BEFORE Angular navigates to a route that uses this guard
export const authGuard: CanActivateFn = (route, state) => {

  // Get the singleton AuthService instance
  // Angular's DI system provides the same instance everywhere
  const authService = inject(AuthService);

  // Get Router instance so we can programmatically navigate
  const router = inject(Router);

  // Read JWT token from localStorage via AuthService
  // If user is logged in, this token should exist
  const token = authService.getToken();

  // If token exists → user is authenticated
  // Allow Angular to activate (open) the requested route
  if (token) {
    return true; // navigation continues
  }

  // If token does NOT exist → user is not logged in
  // Redirect the user to the login page
  router.navigate(['/login']);

  // Block the current navigation
  // Angular will NOT load the protected component
  return false;
};
