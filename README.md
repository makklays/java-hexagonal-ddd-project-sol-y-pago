# Project Sol y Pago ☀️💶💸 - Java 

A production-ready FinTech Payment Gateway microservice built with **Java 17**, **Spring Boot 3**, **Domain-Driven Design (DDD)**, and **Hexagonal Architecture (Ports & Adapters)**.

The project name **"Sol y Pago"** translates from Spanish as *"Sun and Payment"*, reflecting a modern Spanish FinTech startup ecosystem.

<p align="left">
  <img src="doc/images/hexagonal_architecture1.png" width="400" alt="Sol y Pago 1" />
  <img src="doc/images/hexagonal_architecture3.png" width="400" alt="Sol y Pago 2" />
</p>

---

## 🏛️ Architecture Blueprint

This project strictly follows **Hexagonal Architecture** principles combined with **DDD patterns**. The source code is decoupled into three isolated Maven modules to enforce architectural boundaries at the compilation level:

1. **`domain` (The Core)**: 100% pure Java 17 code. Contains Aggregate Roots, Entities, Value Objects, and core business rules. Zero dependencies on Spring, Hibernate, or any external framework.
2. **`application` (Use Cases / Ports)**: Orchestrates business workflows. Defines **Inbound Ports** (Use Cases API) and **Outbound Ports** (SPI for DB and Gateways). Depends only on the `domain` module.
3. **`infrastructure` (Adapters)**: Technology-specific layer. Contains **Inbound Adapters** (Spring REST Controllers) and **Outbound Adapters** (Spring Data JPA, PostgreSQL, Stripe/Bizum HTTP Clients).

```text
    [ Client / UI ] 
          │ (HTTP JSON)
          ▼
┌────────────────────────────────────────────────────────┐
│ infrastructure (Adapters)                              │
│    ├── web/ PaymentRestController                      │
│    └── ...                                             │
│         │                                              │
│         ▼                                              │
│   ┌────────────────────────────────────────────────┐   │
│   │ application (Ports & Services)                 │   │
│   │    ├── ports/inbound/ ProcessPaymentUseCase    │   │
│   │    └── services/ ProcessPaymentService         │   │
│   │         │                                      │   │
│   │         ▼                                      │   │
│   │   ┌────────────────────────────────────────┐   │   │
│   │   │ domain (Core / DDD Aggregate)          │   │   │
│   │   │    └── model/ PaymentTransaction ☀️    │   │   │
│   │   └────────────────────────────────────────┘   │   │
│   │         │                                      │   │
│   │         ▼                                      │   │
│   │    └── ports/outbound/ PaymentGatewayPort      │   │
│   └────────────────────────────────────────────────┘   │
│         │                                              │
│         ▼                                              │
│    ├── persistence/ PostgresPaymentRepositoryImpl      │
│    └── gateways/ StripeGatewayImpl / BizumGatewayImpl  │
└────────────────────────────────────────────────────────┘
          │
          ▼
 [ Database / External APIs (Stripe, Bizum) ]
```

---

## ⚙️ Core Business Rules & Routing Engine

The system acts as an intelligent smart-routing gateway for European e-commerce platforms, integrating two primary payment methods: **Stripe** (Global Credit Cards) and **Bizum** (Instant Mobile Payments popular in Spain).

* **Smart Routing Rule**:
    * If the payment amount is **under €50.00** AND the customer is located in Spain (`countryCode: "ES"`), the system automatically routes the payment via **Bizum** to minimize transactional fees.
    * If the amount is **€50.00 or higher**, or the customer is from any other country, the transaction is processed via **Stripe**.
* **Resilience & Fallback**: If the preferred gateway fails or times out, the system automatically triggers a dynamic fallback to the alternative gateway to maximize success rates.

---

## 🛠️ Tech Stack & Prerequisites

* **Backend**: Java 17 (Utilizing Record types and Pattern Matching)
* **Framework**: Spring Boot 3.3+ (Spring Web, Spring Data JPA)
* **Database**: PostgreSQL 16+
* **Build System**: Apache Maven 3.9+
* **Testing**: JUnit 5 (with 100% Mock-free unit testing for the domain core)

---

## 🚀 Getting Started

### 1. Build the project
Run the following command from the root directory to compile all sub-modules:
```bash
mvn clean package
```

### 2. Run the application
Locate the executable JAR in the infrastructure module and run it:
```bash
java -jar infrastructure/target/infrastructure-1.0.0-SNAPSHOT.jar
```

