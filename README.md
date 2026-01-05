# Project & Task Management System

REST API for project and task management with hexagonal architecture, JWT authentication, and React frontend.

## 🚀 Steps to Run the Application

### Prerequisites
- Docker and Docker Compose installed
- Available ports: 5432, 8080, 3000

### Running with Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd assesment

# Start all services
docker-compose up -d

# Verify services are running
docker-compose ps
```

**Available services:**
| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | React Application |
| Backend API | http://localhost:8080/api | REST API |
| Swagger UI | http://localhost:8080/swagger-ui.html | API Documentation |
| PostgreSQL | localhost:5432 | Database |

### Manual Execution (Development)

```bash
# 1. Start database
docker-compose up -d db

# 2. Backend (in another terminal)
cd backend
./mvnw spring-boot:run

# 3. Frontend (in another terminal)
cd frontend
npm install
npm run dev
```

### Running Tests

The project includes **unit tests only** (no integration tests), so you can run them **without Docker or any containers running**:

```bash
cd backend
./mvnw test
```

**Test output:**
```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**Test classes:**
- `ActivateProjectServiceTest` (6 tests) - Tests for activating projects
- `CompleteTaskServiceTest` (8 tests) - Tests for completing tasks

All tests use **JUnit 5 + Mockito** with mocked dependencies, no Spring context is loaded.

---

## 🔐 Test Credentials

### Pre-seeded Users (from database migrations)

| Username | Password | Email |
|----------|----------|-------|
| `admin` | `Test123!` | admin@example.com |
| `johndoe` | `Test123!` | john@example.com |
| `janedoe` | `Test123!` | jane@example.com |

### Database
```
Host: localhost
Port: 5432
Database: projectdb
Username: postgres
Password: postgres
```

### API Authentication

1. **Login with pre-seeded user:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Test123!"}'
```

2. **Register a new user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"myuser","password":"MyPass123!","name":"My User","email":"myuser@example.com"}'
```

3. **Use token in requests:**
```bash
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## 🏗️ Technical Decisions

### Hexagonal Architecture (Ports & Adapters) with 4 Layers

```
src/main/java/com/riwi/assesment/
│
├── domain/                    # 🔵 CORE - Pure business logic
│   ├── model/                 # Domain entities (Project, Task, User)
│   ├── exception/             # Typed domain exceptions
│   └── port/
│       ├── in/                # Input ports (Use Cases)
│       └── out/               # Output ports (Contracts)
│
├── application/               # 🟢 APPLICATION - Use case orchestration
│   └── service/               # Use Case implementations
│
├── infrastructure/            # 🟠 INFRASTRUCTURE - Technical adapters
│   ├── adapter/
│   │   ├── persistence/       # JPA Entities, Repositories, Mappers
│   │   ├── security/          # JWT Provider, User Details
│   │   └── service/           # Audit, Notification adapters
│   └── config/                # Spring Security, OpenAPI, Beans
│
└── presentation/              # 🟣 PRESENTATION - HTTP Interface
    ├── controller/            # REST Controllers (@RestController)
    ├── dto/
    │   ├── request/           # Input DTOs (validations)
    │   └── response/          # Output DTOs (serialization)
    └── exception/             # GlobalExceptionHandler, ProblemDetails
```

**Dependency flow:**
```
Presentation → Application → Domain ← Infrastructure
     ↓              ↓           ↑            ↓
  Controllers    Services    Ports      Adapters
```

**Justification:** 
- **Domain:** No external dependencies, 100% testable
- **Application:** Orchestrates use cases, implements input ports
- **Infrastructure:** Implements output ports (DB, JWT, external services)
- **Presentation:** Handles HTTP, request validation, response serialization

### Security

- **JWT (JSON Web Tokens):** Stateless authentication, 24-hour token duration
- **BCrypt:** Password hashing with automatic salt
- **Spring Security 6:** Configuration with `SecurityFilterChain`
- **Ownership validation:** Only the owner can modify their projects/tasks

### Database

- **PostgreSQL 15:** Robust relational database
- **Flyway:** Versioned migrations (V1-V5)
- **Soft Delete:** Logical deletion with `deleted` field (preserves history)
- **Auditing:** `audit_logs` table for action traceability

### Implemented Patterns

| Pattern | Usage |
|---------|-------|
| **Repository** | Persistence abstraction |
| **DTO** | Layer separation |
| **Mapper** | Entity ↔ Domain ↔ DTO conversion |
| **Use Case** | One use case per business operation |
| **Adapter** | Output port implementations |

### Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.5.9 |
| Security | Spring Security 6, JWT (jjwt 0.12.6) |
| Persistence | Spring Data JPA, PostgreSQL 15, Flyway |
| Documentation | SpringDoc OpenAPI 2.8.9 (Swagger) |
| Frontend | React 19, Vite, TailwindCSS |
| Testing | JUnit 5, Mockito |
| Containers | Docker, Docker Compose |

### Error Handling

- **RFC 7807 (Problem Details):** Standardized error responses
- **GlobalExceptionHandler:** Centralized exception handling
- **Domain exceptions:** Typed for each error case

```json
{
  "type": "https://api.projectmanager.com/errors/project-not-found",
  "title": "Project Not Found",
  "status": 404,
  "detail": "Project with ID 123 was not found",
  "instance": "/api/projects/123"
}
```

### Testing

- **Unit Tests:** JUnit 5 + Mockito without loading Spring Context
- **Mocking:** All output ports mocked
- **Coverage:** Critical use cases (ActivateProject, CompleteTask)

---

## 📁 Project Structure

```
assesment/
├── backend/                   # Spring Boot API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Source code
│   │   │   └── resources/    # Configuration + Migrations
│   │   └── test/             # Unit tests
│   ├── pom.xml               # Maven dependencies
│   └── Dockerfile
├── frontend/                  # React + Vite
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml         # Service orchestration
└── README.md
```

---

## 📚 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login |

### Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | List user's projects |
| POST | `/api/projects` | Create project |
| GET | `/api/projects/{id}` | Get project |
| PUT | `/api/projects/{id}` | Update project |
| DELETE | `/api/projects/{id}` | Delete project (soft delete) |
| POST | `/api/projects/{id}/activate` | Activate project |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects/{projectId}/tasks` | List tasks |
| POST | `/api/projects/{projectId}/tasks` | Create task |
| GET | `/api/tasks/{id}` | Get task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task (soft delete) |
| POST | `/api/tasks/{id}/complete` | Complete task |

---

## 🛠️ Useful Commands

```bash
# View logs for all services
docker-compose logs -f

# View logs for a specific service
docker-compose logs -f backend

# Restart services
docker-compose restart

# Stop services
docker-compose down

# Stop and remove volumes (reset DB)
docker-compose down -v

# Rebuild images
docker-compose up -d --build
```

