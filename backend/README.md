# Flight Booking Microservices System

<img width="3536" height="1951" alt="image" src="https://github.com/user-attachments/assets/7bce6fdc-38e4-4279-94fb-944a64f5a981" />

This project is a microservice-based implementation of a flight booking platform. It includes separate services for flight management and booking operations, along with supporting components like API Gateway, Config Server, Eureka, RabbitMQ, and OpenFeign for inter-service communication.

The goal of the project is to learn and apply modern Spring Cloud patterns including service discovery, load balancing, centralized configuration, and event-driven architecture.

---
## ER DIAGRAM
[View on Eraser![](https://app.eraser.io/workspace/bzh7FeGzkGMRfzognglP/preview?elements=blwO8lnppYnb8shYCmBBmA&type=embed)](https://app.eraser.io/workspace/bzh7FeGzkGMRfzognglP?elements=blwO8lnppYnb8shYCmBBmA
## Overview

The system consists of the following services:

1. **Flight Service** – Manages airlines, flight schedules, inventory, and search.
2. **Booking Service** – Handles ticket bookings, cancellations, passenger info, PNR generation, and booking history.
3. **API Gateway** – The single entry point to route all client requests.
5. **Eureka Server** – Service registry used for discovering microservices dynamically.
6. **Config Server** – Centralized configuration storage for all microservices.
7. **Load Balencing** - Utilized Open feign and spring load balencer to provide load balencing in Flight-booking service.
8. **Circuit Breaker** - Implemented circuit breaker to fallback if service is down.
9. **RabbitMQ** – Used to publish an event whenever a booking is completed( Ongoing )

Each service has its own MySQL database and is designed to run independently.

---

## Architecture (High-Level)

```
                 API Gateway
                     |
          ---------------------------
          |                         |
   Flight Service            Booking Service
          |                         |
      MySQL DB                  MySQL DB
          |                         |
         Eureka Server (Service Discovery)
                     |
                  RabbitMQ
          (Booking events published here)
```

Inter-service calls (Booking → Flight) are done using OpenFeign with client-side load balancing provided by Spring Cloud LoadBalancer.

---

## Technologies Used

* Java 17
* Spring Boot 3.x
* Spring Cloud (Config, Eureka, OpenFeign, LoadBalancer, Gateway)
* RabbitMQ
* MySQL
* Maven
* JMeter (performance testing)
* Postman CLI (Newman) for API tests

---

## Microservice Responsibilities

### 1. Flight Service

* Add airline
* Add flight inventory
* Search flights by date, source, and destination
* Provide flight details to booking service
* Maintains available and total seats

### 2. Booking Service

* Create a booking for a selected flight
* Validate flight using OpenFeign call
* Store passenger details
* Maintain booking history and ticket retrieval
* Publish a booking-created event to RabbitMQ
* Handle cancellation (only allowed 24 hours before the journey)

### 3. Eureka Server

* Registers and discovers all running microservices
* Removes the need to hard-code URLs

### 4. Config Server

* Hosts application configuration (e.g., in Git)
* All services fetch config from here

### 5. API Gateway

* Routes `/api/v1.0/flight/**` to Flight Service
* Routes `/api/v1.0/flight/booking/**` to Booking Service
* Helps in centralized routing and potential future cross-cutting concerns

### 6. RabbitMQ

* Booking service publishes a JSON message whenever a booking is successful
* Future services (email, SMS, analytics) can consume these messages

---

## How to Run the System (In Order)

### 1. Start Config Server

```
cd config-server
mvn spring-boot:run
```

### 2. Start Eureka Server

```
cd eureka-server
mvn spring-boot:run
```

### 3. Start RabbitMQ (Docker)

```
docker run -d --hostname rabbit --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
```

### 4. Start Flight Service

```
cd flight-service
mvn spring-boot:run
```

### 5. Start Booking Service

```
cd booking-service
mvn spring-boot:run
```

### 6. Start API Gateway

```
cd api-gateway
mvn spring-boot:run
```

---

## Running Postman Tests from Terminal (Newman)

Export your Postman collection as `collection.json`
Export environment (if required) as `environment.json`

Run the collection:

```
newman run collection.json
```

With environment:

```
newman run collection.json -e environment.json
```

Generate an HTML report:

```
newman run collection.json -e environment.json -r html --reporter-html-export result.html
```

---

## Running JMeter Tests from Terminal

Assuming JMeter `.jmx` file is located at `/tests/flight_test.jmx`.

### Basic run

```
jmeter -n -t tests/flight_test.jmx -l results.jtl
```

### Run with HTML report generation

```
jmeter -n -t tests/flight_test.jmx -l results.jtl -e -o ./report/
```

Options used:

* `-n` → non-GUI mode
* `-t` → test file
* `-l` → log results
* `-e` → generate report
* `-o` → output directory

---

## What I Implemented

* Two microservices with separate databases
* Service discovery using Eureka
* API Gateway routing for all APIs
* Config Server for centralized configuration
* OpenFeign for clean inter-service communication
* Load balancing using Spring Cloud LoadBalancer
* RabbitMQ event publishing when booking completes
* Request validation across services
* Database design for Flights, Airlines, Bookings, Passengers, Users
* Error handling and input validation
* Support for JMeter and Postman automated tests


