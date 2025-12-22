@echo off

echo Starting Eureka Server...
start cmd /k java -jar eureka-server\target\eureka-server-1.0.0.jar

echo Waiting 10 seconds for Eureka to initialize...
timeout /t 10 /nobreak > nul

echo Starting API Gateway...
start cmd /k java -jar security-api-gateway\target\security-api-gateway-1.0.0.jar

echo Starting Booking Service...
start cmd /k java -jar booking-service\target\booking-service-1.0.0.jar

echo Starting Flight Service...
start cmd /k java -jar flight-service\target\flight-service-1.0.0.jar

echo All services launched 
