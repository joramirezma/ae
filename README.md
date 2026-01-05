# 🚀 Project & Task Management System

A full-stack application for managing projects and tasks, built with **Clean Architecture** and **Hexagonal Architecture** patterns.

## 📋 Table of Contents

- [Technologies](#-technologies)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Running Tests](#-running-tests)
- [Docker Deployment](#-docker-deployment)
- [Technical Decisions](#-technical-decisions)
- [Project Structure](#-project-structure)

## 🛠 Technologies

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.5.9** - Application framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Database
- **JWT (jjwt 0.12.6)** - Token-based authentication
- **Springdoc OpenAPI 2.7.0** - API documentation (Swagger)
- **JUnit 5 + Mockito** - Unit testing

### Frontend
- **React 19** - UI library
- **Vite** - Build tool
- **React Router DOM** - Client-side routing
- **Native Fetch API** - HTTP requests

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## 🏗 Architecture

This project follows **Clean Architecture** and **Hexagonal Architecture** (Ports & Adapters) patterns:

```
┌─────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE                            │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────────┐   │
│  │ Controllers │  │ JPA Entities │  │ Security Config   │   │
│  │   (REST)    │  │ Repositories │  │ JWT Provider      │   │
│  └──────┬──────┘  └──────┬───────┘  └───────────────────┘   │
│         │                │                                   │
├─────────┼────────────────┼───────────────────────────────────┤
│         │   APPLICATION  │                                   │
│         │  ┌─────────────▼──────────────┐                   │
│         │  │        Use Cases           │                   │
│         │  │  (CreateProject, etc.)     │                   │
│         │  └─────────────┬──────────────┘                   │
│         │                │                                   │
├─────────┼────────────────┼───────────────────────────────────┤
│         │     DOMAIN     │                                   │
│         │  ┌─────────────▼──────────────┐                   │
│         └──►   Entities & Ports         │                   │
│            │  (User, Project, Task)     │                   │
│            │  (Input/Output Ports)      │                   │
│            └────────────────────────────┘                   │
└─────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer | Responsibility |
|-------|----------------|
| **Domain** | Business entities, ports (interfaces), domain exceptions |
| **Application** | Use case implementations, business logic orchestration |
| **Infrastructure** | External adapters (DB, REST, Security), framework configs |

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 15+ (or Docker)
- Maven 3.8+

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/joramirezma/ae.git
cd ae

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost:5173
# Backend API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Option 2: Manual Setup

#### 1. Start PostgreSQL

```bash
# Using Docker
docker run -d \
  --name postgres-db \
  -e POSTGRES_DB=assesment_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

#### 2. Start Backend

```bash
cd backend

# Build the project
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run
```

#### 3. Start Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

### Default Credentials

Create a new user through the registration page or API:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "email": "admin@test.com", "password": "admin123"}'
```

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Projects

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/projects` | List all projects | ✅ |
| GET | `/api/projects/{id}` | Get project by ID | ✅ |
| POST | `/api/projects` | Create new project | ✅ |
| PATCH | `/api/projects/{id}/activate` | Activate project | ✅ |

### Tasks

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/tasks` | List all tasks | ✅ |
| GET | `/api/tasks/project/{projectId}` | Get tasks by project | ✅ |
| POST | `/api/tasks` | Create new task | ✅ |
| PATCH | `/api/tasks/{id}/complete` | Mark task as completed | ✅ |

### Swagger Documentation

Access the interactive API documentation at: `http://localhost:8080/swagger-ui.html`

## 🧪 Running Tests

```bash
cd backend

# Run all tests
./mvnw test

# Run with coverage report
./mvnw test jacoco:report
```

### Test Coverage

The project includes **13 unit tests** covering:

- `ActivateProjectServiceTest` - 6 tests
- `CompleteTaskServiceTest` - 7 tests

Tests use **JUnit 5** and **Mockito** for mocking dependencies.

## 🐳 Docker Deployment

### Build and Run

```bash
# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | Database host |
| `DB_PORT` | 5432 | Database port |
| `DB_NAME` | assesment_db | Database name |
| `DB_USERNAME` | postgres | Database user |
| `DB_PASSWORD` | postgres | Database password |
| `JWT_SECRET` | (auto-generated) | JWT signing key |
| `JWT_EXPIRATION` | 86400000 | Token expiration (24h) |

## 💡 Technical Decisions

### 1. Clean Architecture + Hexagonal Pattern

**Why:** Separation of concerns, testability, and flexibility to change infrastructure without affecting business logic.

**Implementation:**
- Domain layer has no dependencies on frameworks
- Use cases define input/output ports
- Infrastructure adapters implement ports

### 2. JWT Stateless Authentication

**Why:** Scalable, no server-side session storage required.

**Implementation:**
- Token generated on login with 24h expiration
- Token validated on each request via filter
- Claims stored in token (userId, username)

### 3. Native Fetch API (No Axios)

**Why:** Reduces bundle size, native browser API, sufficient for this use case.

### 4. PostgreSQL with JPA

**Why:** Robust RDBMS with excellent Spring Data support, automatic schema generation with `ddl-auto: update`.

### 5. Multi-stage Docker Build

**Why:** Smaller final image (~300MB vs ~700MB), security (no build tools in production).

### 6. Project Status Workflow

**Why:** Business rule requiring project activation before task assignment.

**Flow:** `PENDING → ACTIVE` (only active projects can have tasks)

## 📁 Project Structure

```
assesment/
├── backend/
│   ├── src/main/java/com/riwi/assesment/
│   │   ├── domain/
│   │   │   ├── model/           # Entities (User, Project, Task)
│   │   │   ├── exception/       # Domain exceptions
│   │   │   └── port/
│   │   │       ├── in/          # Input ports (Use case interfaces)
│   │   │       └── out/         # Output ports (Repository interfaces)
│   │   ├── application/
│   │   │   └── service/         # Use case implementations
│   │   └── infrastructure/
│   │       ├── adapter/
│   │       │   ├── in/rest/     # REST controllers
│   │       │   └── out/persistence/  # JPA adapters
│   │       ├── config/          # Spring configurations
│   │       └── security/        # JWT security components
│   ├── src/test/java/           # Unit tests
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/          # React components
│   │   ├── context/             # Auth context
│   │   ├── pages/               # Page components
│   │   └── services/            # API service (fetch)
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml
└── README.md
```

## 📄 License

This project is created for the Riwi Employability Assessment.

---

**Author:** Jorge Ramirez  
**Date:** January 2026
