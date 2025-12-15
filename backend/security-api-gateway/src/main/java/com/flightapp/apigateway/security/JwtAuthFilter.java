package com.flightapp.apigateway.security;

import com.flightapp.apigateway.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();
        
     // ✅ Allow all CORS preflight requests
        if (method == HttpMethod.OPTIONS) {
        	System.out.println("CORS fix hit!!_____________________________________________________________________________________________");
            return chain.filter(exchange);
        }

        // 1. Public endpoints (no token required)
        if (isPublicEndpoint(path, method)) {
            return chain.filter(exchange);
        }

        // 2. Get token from header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            return unauthorized(exchange, "Invalid or expired JWT token");
        }

        String email = jwtUtil.getEmail(token);
        List<String> roles = jwtUtil.getRoles(token);

        // 3. Authorize based on path + role
        if (!isAuthorized(path, method, roles)) {
            return forbidden(exchange, "Access denied");
        }

        // 4. Optionally forward user info to downstream services
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Email", email)
                        .header("X-User-Roles", String.join(",", roles)))
                .build();

        return chain.filter(mutatedExchange);
    }

    // Everyone:
    // - /auth/**
    // - POST /api/v1.0/flight/search
    // - GET /api/v1.0/flight/airline/all
    // - GET /api/v1.0/flight/{flightId}
    private boolean isPublicEndpoint(String path, HttpMethod method) {
        if (path.startsWith("/auth/")) {
            return true;
        }

        if (path.equals("/api/v1.0/flight/search") && method == HttpMethod.POST) {
            return true;
        }

        if (path.equals("/api/v1.0/flight/airline/all") && method == HttpMethod.GET) {
            return true;
        }

        // GET /api/v1.0/flight/{flightId} (flight details)
        if (path.matches("^/api/v1.0/flight/\\d+$") && method == HttpMethod.GET) {
            return true;
        }

        return false;
    }

    private boolean isAuthorized(String path, HttpMethod method, List<String> roles) {
        boolean isUser = roles.contains(UserRole.ROLE_USER.name());
        boolean isAdmin = roles.contains(UserRole.ROLE_ADMIN.name());

        // Admin-only endpoints:
        // - POST /api/v1.0/flight/airline/add
        // - GET /api/v1.0/flight/airline/{id}  (airline details)
        // - POST /api/v1.0/flight/airline/inventory/add
        // - PUT /api/v1.0/flight/{flightId}/**  (seat ops)
        if (path.equals("/api/v1.0/flight/airline/add")) {
            return isAdmin;
        }
        if (path.matches("^/api/v1.0/flight/airline/\\d+$")) {
            return isAdmin;
        }
        if (path.equals("/api/v1.0/flight/airline/inventory/add")) {
            return isAdmin;
        }
        if (path.matches("^/api/v1.0/flight/\\d+/.+")
                && method == HttpMethod.PUT) {
            return isAdmin;
        }

        // Booking endpoints (USER or ADMIN):
        // - POST /api/v1.0/flight/booking/{flightId}
        // - GET /api/v1.0/flight/ticket/{pnr}
        // - GET /api/v1.0/flight/booking/history/{emailId}
        // - DELETE /api/v1.0/flight/booking/cancel/{pnr}
        if (path.matches("^/api/v1.0/flight/booking/\\d+$")
                && method == HttpMethod.POST) {
            return isUser || isAdmin;
        }
        if (path.matches("^/api/v1.0/flight/ticket/.+")
                && method == HttpMethod.GET) {
            return isUser || isAdmin;
        }
        if (path.matches("^/api/v1.0/flight/booking/history/.+")
                && method == HttpMethod.GET) {
            return isUser || isAdmin;
        }
        if (path.matches("^/api/v1.0/flight/booking/cancel/.+")
                && method == HttpMethod.DELETE) {
            return isUser || isAdmin;
        }

        // Any other path: require at least USER (or ADMIN)
        return isUser || isAdmin;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        // Ensure this runs early
        return -1;
    }
}
