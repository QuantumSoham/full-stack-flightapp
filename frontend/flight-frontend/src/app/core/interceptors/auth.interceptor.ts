// Here I have written a Functional HTTP interceptor
// This runs automatically for every outgoing HttpClient request
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // Read JWT token directly from localStorage Token is saved here after successful login
  const token = localStorage.getItem('token');

  // If token exists, user is logged in
  // We need to attach it to the request so backend can authenticate
  if (token) {

    // HttpRequest objects are immutable
    // clone() creates a new request with modified headers
    req = req.clone({
      setHeaders: {
        // Add Authorization header in standard Bearer format
        // API Gateway expects this header for protected endpoints
        Authorization: `Bearer ${token}`,
      },
    });
  }

  // Pass the (original or modified) request forward
  // next(req) sends it to the backend
  // If this interceptor didn’t exist, request would go without JWT
  return next(req);
};
