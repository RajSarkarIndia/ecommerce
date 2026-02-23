# 🛒 E-Commerce Microservices Application

A full-stack e-commerce application built using Spring Boot Microservices, JWT Authentication, Stripe Payments, and Google Cloud Storage, with an Angular frontend.

This project demonstrates a production-style microservice architecture with independent services for authentication, products, orders, and payments.

---

# 🏗️ Architecture Overview

## Microservices

### 🔐 Authentication Service
- User registration & login
- JWT token generation
- Address management

### 📦 Product Service
- Product CRUD operations
- Product image upload
- Google Cloud Storage integration

### 📑 Order Service
- Create orders
- Order items mapping
- Order history tracking

### 💳 Payment Service
- Stripe payment integration
- Webhook handling
- Payment status tracking

---

# 🧱 Tech Stack

## Backend
- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL
- JWT
- Stripe API
- Google Cloud Storage

## Frontend
- Angular
- LocalStorage-based Cart

## Cloud
- Google Cloud Storage (Product Images)

---

# 📌 Core ERD Entities

- User
- Address
- Product
- ProductImages
- Order
- OrderItem
- Payment

Relationships Overview:

- One User → Many Orders
- One Order → Many OrderItems
- One Product → Many ProductImages
- One Order → One Payment

---

![E-Commerce ERD](images/erd.svg)



# 📦 Required Environment Variables

Before running the project, export the following environment variables:

```bash
export ProjectId=
export BucketName=

export dbUsername=postgres
export dbPassword=

export stripeKey=

export webhookSecret=

export GOOGLE_APPLICATION_CREDENTIALS=(Full Location)
```

---

## 🔎 Environment Variable Description

| Variable | Description |
|-----------|------------|
| ProjectId | Google Cloud Project ID |
| BucketName | GCS bucket for storing product images |
| dbUsername | PostgreSQL username |
| dbPassword | PostgreSQL password |
| stripeKey | Stripe secret API key |
| webhookSecret | Stripe webhook signing secret |
| GOOGLE_APPLICATION_CREDENTIALS | Full path to GCP service account JSON file |

Example:

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/home/user/gcp/service-account.json
```

---

# 🗄️ Database Setup (DDL)

PostgreSQL is required.

Create databases (recommended one per service):

```sql
CREATE DATABASE auth_db;
CREATE DATABASE product_db;
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
```

Each microservice connects to its respective database.

---

# 🛍️ Cart Strategy

- Cart is stored in localStorage (Frontend)
- During checkout, cart items are converted into:
  - Order
  - OrderItem
- No persistent Cart table in backend

---

# 🚀 How to Run

## 1️⃣ Export Environment Variables

```bash
source env.sh
```

Or export manually as shown above.

---

## 2️⃣ Run Backend Microservices

From each service directory:

```bash
mvn spring-boot:run
```

Or run directly from your IDE.

---

## 3️⃣ Run Angular Frontend

```bash
npm install
ng serve
```

Frontend will run at:

```
http://localhost:4200
```

---

# 🔐 Authentication Flow

1. User logs in
2. JWT token is generated
3. Token is sent via Authorization header
4. Each microservice validates JWT before processing requests

---

# 💳 Payment Flow

1. User places an order
2. Stripe Checkout Session is created
3. User completes payment on Stripe
4. Stripe sends webhook event
5. Payment status is updated in database

---

# ☁️ Google Cloud Storage Setup

1. Create a Google Cloud project
2. Create a Storage bucket
3. Create a Service Account
4. Download the JSON credentials file
5. Set:

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/your/service-account.json
```

---

# 🎯 Key Learning Concepts Demonstrated

- Microservice architecture
- JWT-based authentication
- Stripe payment integration
- Secure webhook validation
- Cloud storage integration
- Entity relationship modeling
- Service separation principles

---

# ⚠️ Future Improvements

- Persistent Cart Service
- Inventory Management Service
- API Gateway
- Docker & Kubernetes deployment
- CI/CD pipeline
- Distributed tracing & logging

---

# 👨‍💻 Author

Raj Sarkar
