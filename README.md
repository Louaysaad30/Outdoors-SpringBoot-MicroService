# 🛠️ Backend Microservices Project - README

## 🚀 Outdoor Adventures Platform - Backend Microservices

A robust and scalable microservices-based backend system powering the Outdoor Adventures Platform. This architecture supports a full suite of services such as camping reservations, event participation, transport bookings, training programs, user engagement forums, and a marketplace for outdoor gear.

![Microservices Architecture](https://via.placeholder.com/800x400?text=Outdoor+Adventures+Microservices+Architecture)

## 📋 Table of Contents
- [🏗 System Architecture](#-system-architecture)
- [🧩 Microservices Overview](#-microservices-overview)
- [💻 Technologies Used](#-technologies-used)
- [✨ Core Features](#-core-features)
- [🛠 Installation and Setup](#-installation-and-setup)
- [📚 API Documentation](#-api-documentation)
- [📄 License](#-license)

## 🏗 System Architecture

The system adheres to a modern microservices architecture and includes:

- **Service Registry (Eureka Server)** – Manages service discovery and registration.
- **RESTful APIs** – Each microservice provides dedicated endpoints.
- **Database per Service** – Promotes decoupling and data encapsulation.
- **External Integrations** – Incorporates AI/NLP features through Hugging Face and Perspective API.

## 🧩 Microservices Overview

The platform consists of the following independently deployed services:

1. **Camping-Service** – Manages campsites, accommodation, inventory, and bookings.
2. **Event-Service** – Event creation and discovery with smart content enhancement via NLP.
3. **Formation-Service** – Training course management and progress tracking.
4. **Forum-Service** – Community interactions with automated moderation and AI-powered features.
5. **MarketPlace-Service** – Online shopping for camping gear with payment processing and invoicing.
6. **Transport-Service** – Vehicle listings, rentals, and reservations.
7. **User-Service** – Authentication, role-based authorization, messaging, and analytics.

## 💻 Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Cloud** (Eureka, Config)
- **Netflix Eureka**
- **Spring Data JPA**
- **MySQL**
- **Swagger/OpenAPI** – For interactive API documentation
- **Maven** – Build and dependency management
- **Docker & Docker Compose** – Containerization (optional deployment)
- **Cloudinary** – Media file storage and retrieval
- **Hugging Face** – Sentiment analysis and AI-generated text
- **Perspective API** – Toxicity and content moderation

## ✨ Core Features

### Camping Service
- Manage camping sites and lodging
- Book equipment and locations
- Analyze reviews with sentiment scores
- Display geolocations on maps

### Event Service
- AI-enhanced event descriptions
- Keyword extraction and tagging
- Event calendar and management dashboard

### Forum Service
- Full-fledged discussion threads
- AI-generated replies and moderation
- Multi-language post translation

### Training (Formation) Service
- Course management and enrollment
- Learning materials distribution
- Progress analytics

### MarketPlace Service
- Product listings with photos and prices
- Shopping cart and order tracking
- PDF invoice generation
- Customer review system

### Transport Service
- Vehicle rental interface
- Route and reservation planning
- Review system for feedback

### User Service
- Secure registration and login
- JWT-based authentication
- Real-time messaging and notifications
- Admin roles and statistics dashboard

## 🛠 Installation and Setup

### Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- (Optional) Docker & Docker Compose

### Setup Guide

1. **Clone the project**
```bash
git clone https://github.com/Louaysaad30/Outdoors-SpringBoot-MicroService.git
```

2. **Prepare MySQL databases**
   - Create separate schemas for each microservice
   - Update each service’s `application.properties` with credentials

3. **Start Eureka Server**
```bash
cd Eureka-Server
mvn spring-boot:run
```

4. **Run individual services**
```bash
cd Camping-Service
mvn spring-boot:run
# Repeat for each service
```

## 📚 API Documentation

Swagger documentation available at:

- **Camping Service**: http://localhost:9092/swagger-ui.html
- **Event Service**: http://localhost:9095/swagger-ui.html
- **Forum Service**: http://localhost:9090/swagger-ui.html
- **MarketPlace Service**: http://localhost:9091/swagger-ui.html
- **Transport Service**: http://localhost:9093/swagger-ui.html
- **User Service**: http://localhost:9098/swagger-ui.html

## 📄 License

Licensed under the MIT License. See the `LICENSE` file for more details.
