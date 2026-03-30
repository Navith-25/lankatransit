# 🚌 LankaTransit Backend API

LankaTransit is a comprehensive, cloud-native backend system designed to revolutionize the bus transport industry in Sri Lanka. Built with a vision similar to ride-hailing giants like Uber and PickMe, this API serves as the core engine powering real-time bus tracking, digital ticketing, and multi-tier fleet management.

## ✨ Key Features

* **🔐 Robust Authentication & Authorization:** Secured via JWT (JSON Web Tokens) with strict Role-Based Access Control (RBAC) for Admins, Bus Owners, Drivers, and Passengers.
* **🗺️ Route & Halt Management:** Dynamic management of complex bus routes, individual halts, and trip schedules.
* **🎫 Digital Ticketing & Passes:** Automated ticket generation, booking management, and subscription pass handling.
* **🚌 Fleet Management:** Comprehensive endpoints for bus registration, driver assignment, and owner document verification.
* **☁️ Cloud-Ready & Containerized:** Fully containerized using Docker and optimized for deployment on cloud environments (e.g., Hugging Face Spaces) with external cloud database integration.
* **📂 File Storage Integration:** Built-in service (`FileStorageService`) to handle user uploads, bus documentation, and profile imagery.

## 🛠️ Tech Stack & Architecture

* **Framework:** Java 17 / Spring Boot 3.x
* **Security:** Spring Security & JWT
* **Database:** Relational Database (MySQL/PostgreSQL via Spring Data JPA)
* **Deployment:** Docker
* **Build Tool:** Maven

## 🚦 Getting Started

### Prerequisites
* Java 17+
* Docker (optional, but recommended for consistent environments)
* Maven

### Local Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd lankatransit
