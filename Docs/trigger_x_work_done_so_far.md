# TriggerX – Work Done So Far (Technical Documentation)

## 1. Project Overview
**TriggerX** is a microservice-based backend system designed to support authentication, event triggering, email notifications, and scalable integrations. The architecture follows industry-standard patterns such as **API Gateway**, **Service Registry**, **Event-Driven Communication**, and **Stateless Services**.

Primary goals so far:
- Build a **real-world microservices architecture**
- Implement **secure authentication** with JWT
- Use **Kafka** for async communication
- Use **Redis** for fast, temporary data (OTP, caching)
- Prepare the system for **scalability, CI/CD, and Dockerization**

---

## 2. High-Level Architecture

### Core Components
1. **API Gateway**
2. **Service Registry (Eureka)**
3. **Auth Service**
4. **User Service**
5. **Trigger / Email Event Flow**
6. **Kafka Cluster**
7. **Redis**
8. **Node.js SMTP Service**

### Communication Style
- **Client → API Gateway → Microservices** (REST)
- **Service ↔ Service** (via Eureka discovery)
- **Async events** via Kafka
- **Temporary state** via Redis

---

## 3. Service Registry (Eureka Server)

### What We Implemented
- Central **Eureka Server** to register all microservices
- All services configured with:
  - `spring.application.name`
  - Eureka client enabled

### Benefits Achieved
- No hardcoded service URLs
- Dynamic service discovery
- Easy horizontal scaling

---

## 4. API Gateway

### Responsibilities
- Single entry point for all clients
- Route requests to appropriate services using **service name**
- Central place for:
  - Authentication
  - Authorization
  - Rate limiting (future)
  - Logging (future)

### Current Status
- Gateway successfully registers with Eureka
- Routes working for:
  - Auth service endpoints

---

## 5. Authentication Service (Auth Service)

### Features Implemented

#### 5.1 User Registration
- Endpoint: `/auth/register`
- Flow:
  1. Accepts email + password
  2. Password encrypted
  3. User saved in database

#### 5.2 Login with JWT
- Endpoint: `/auth/login`
- Flow:
  1. Validate credentials
  2. Generate **JWT access token**
  3. Return token to client

#### 5.3 Email OTP Flow (Major Milestone)

**End-to-end OTP pipeline implemented**

Flow:
1. User initiates registration / verification
2. Auth service generates OTP
3. OTP stored in **Redis** with TTL
4. Auth service publishes **Kafka event**
5. Node.js SMTP service consumes event
6. Email sent to user
7. User submits OTP
8. Auth service validates OTP from Redis

Key Points:
- OTP is **never stored in DB**
- Redis TTL ensures auto-expiry
- Fully async and scalable

---

## 6. Kafka Integration

### What We Did
- Set up Kafka producers in Spring Boot
- Defined **email event topics**
- Implemented Kafka consumers

### Why Kafka?
- Decouples services
- Non-blocking email sending
- Fault-tolerant
- Easily extendable (SMS, WhatsApp, Push later)

### Current Kafka Usage
- Auth Service → publishes email events
- Node.js service → consumes email events

---

## 7. Redis Integration

### Use Cases
1. **OTP storage**
2. **Temporary flags / states** (future)
3. **Caching** (planned)

### OTP Design
- Key format: `OTP:email`
- Value: hashed OTP
- TTL: configured (e.g., 5 minutes)

Benefits:
- Extremely fast
- Auto cleanup
- Stateless services

---

## 8. Node.js SMTP Service

### Purpose
- Dedicated service for sending emails
- Keeps Java services clean and fast

### Responsibilities
- Consume Kafka email events
- Send emails using SMTP
- Handle retries (future)

### Why Separate Service?
- Language flexibility
- Easier scaling
- Fault isolation

---

## 9. Database Design (So Far)

### User Table
- `id`
- `email`
- `password`
- `created_at`
- `updated_at`
- Status flags (enabled / verified – planned)

### Design Decisions
- No OTP in DB
- Flags will be used instead of deletes

---

## 10. Security

### Implemented
- JWT-based authentication
- Password encryption

### Planned
- Gateway-level JWT validation
- Role-based authorization
- Refresh tokens

---

## 11. Docker & DevOps (Current Status)

### What We Discussed
- Dockerizing **all services together at the end**
- Use **Docker Compose** for local orchestration
- Avoid premature CI/CD complexity

### Pending
- Dockerfile for each service
- `docker-compose.yml`
- Environment-based configs

---

## 12. What We Have Proven Already

- Real microservices setup (not monolith)
- Service discovery works
- API gateway routing works
- Kafka pub/sub works
- Redis TTL logic works
- Async email delivery works

This is **interview-grade architecture**, not tutorial-level.

---

## 13. Immediate Next Steps

### Technical
- Caching & pagination
- DB flags (soft delete, enable/disable)
- Circuit breaker & resilience
- Centralized logging

### Architecture
- Docker Compose
- CI/CD (later)
- Monitoring (Prometheus / Grafana – optional)

---

## 14. Long-Term Vision for TriggerX

TriggerX can evolve into:
- Notification platform
- Event-driven automation engine
- SaaS-ready backend

---

**Status**: Solid foundation complete. Complexity is expected at this stage — this is exactly how real systems feel.

You are not behind. You are building correctly.

