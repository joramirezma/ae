# Sistema de Gestión de Proyectos y Tareas

API REST para gestión de proyectos y tareas con arquitectura hexagonal, autenticación JWT y frontend React.

## 🚀 Pasos para Ejecutar la Aplicación

### Requisitos Previos
- Docker y Docker Compose instalados
- Puertos disponibles: 5432, 8080, 3000

### Ejecución con Docker Compose (Recomendado)

```bash
# Clonar el repositorio
git clone <repository-url>
cd assesment

# Iniciar todos los servicios
docker-compose up -d

# Verificar que los servicios estén corriendo
docker-compose ps
```

**Servicios disponibles:**
| Servicio | URL | Descripción |
|----------|-----|-------------|
| Frontend | http://localhost:3000 | Aplicación React |
| Backend API | http://localhost:8080/api | API REST |
| Swagger UI | http://localhost:8080/swagger-ui.html | Documentación API |
| PostgreSQL | localhost:5432 | Base de datos |

### Ejecución Manual (Desarrollo)

```bash
# 1. Iniciar base de datos
docker-compose up -d db

# 2. Backend (en otra terminal)
cd backend
./mvnw spring-boot:run

# 3. Frontend (en otra terminal)
cd frontend
npm install
npm run dev
```

### Ejecutar Tests

```bash
cd backend
./mvnw test
```

---

## 🔐 Credenciales de Prueba

### Usuario Administrador
```
Email: admin@test.com
Password: Admin123!
```

### Usuario Regular
```
Email: user@test.com
Password: User123!
```

### Base de Datos
```
Host: localhost
Port: 5432
Database: projectdb
Username: postgres
Password: postgres
```

### Autenticación API

1. **Registrar usuario:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!","name":"Test User"}'
```

2. **Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'
```

3. **Usar token en requests:**
```bash
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## 🏗️ Decisiones Técnicas

### Arquitectura Hexagonal (Ports & Adapters)

```
src/main/java/com/riwi/assesment/
├── domain/                    # Núcleo de negocio (sin dependencias externas)
│   ├── model/                 # Entidades de dominio (Project, Task, User)
│   ├── exception/             # Excepciones de dominio
│   └── port/
│       ├── in/                # Puertos de entrada (Use Cases)
│       └── out/               # Puertos de salida (Repositories, Services)
├── application/
│   └── service/               # Implementación de casos de uso
├── infrastructure/
│   ├── adapter/
│   │   ├── persistence/       # Adaptadores JPA (Entities, Repositories)
│   │   ├── security/          # Adaptador JWT
│   │   └── service/           # Adaptadores de servicios externos
│   └── config/                # Configuración Spring
└── presentation/
    ├── controller/            # REST Controllers
    └── dto/                   # Request/Response DTOs
```

**Justificación:** Permite independencia del framework, facilita testing y mantiene el dominio libre de dependencias técnicas.

### Seguridad

- **JWT (JSON Web Tokens):** Autenticación stateless, tokens de 24h de duración
- **BCrypt:** Hash de contraseñas con salt automático
- **Spring Security 6:** Configuración con `SecurityFilterChain`
- **Validación de ownership:** Solo el propietario puede modificar sus proyectos/tareas

### Base de Datos

- **PostgreSQL 15:** Base de datos relacional robusta
- **Flyway:** Migraciones versionadas (V1-V5)
- **Soft Delete:** Borrado lógico con campo `deleted` (preserva historial)
- **Auditoría:** Tabla `audit_logs` para trazabilidad de acciones

### Patrones Implementados

| Patrón | Uso |
|--------|-----|
| **Repository** | Abstracción de persistencia |
| **DTO** | Separación entre capas |
| **Mapper** | Conversión Entity ↔ Domain ↔ DTO |
| **Use Case** | Un caso de uso por operación de negocio |
| **Adapter** | Implementaciones de puertos de salida |

### Stack Tecnológico

| Capa | Tecnología |
|------|------------|
| Backend | Java 17, Spring Boot 3.5.9 |
| Seguridad | Spring Security 6, JWT (jjwt 0.12.6) |
| Persistencia | Spring Data JPA, PostgreSQL 15, Flyway |
| Documentación | SpringDoc OpenAPI 2.8.9 (Swagger) |
| Frontend | React 19, Vite, TailwindCSS |
| Testing | JUnit 5, Mockito |
| Contenedores | Docker, Docker Compose |

### Manejo de Errores

- **RFC 7807 (Problem Details):** Respuestas de error estandarizadas
- **GlobalExceptionHandler:** Manejo centralizado de excepciones
- **Excepciones de dominio:** Tipadas para cada caso de error

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

- **Unit Tests:** JUnit 5 + Mockito sin cargar Spring Context
- **Mocking:** Todos los puertos de salida mockeados
- **Cobertura:** Casos de uso críticos (ActivateProject, CompleteTask)

---

## 📁 Estructura del Proyecto

```
assesment/
├── backend/                   # API Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Código fuente
│   │   │   └── resources/    # Configuración + Migraciones
│   │   └── test/             # Tests unitarios
│   ├── pom.xml               # Dependencias Maven
│   └── Dockerfile
├── frontend/                  # React + Vite
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml         # Orquestación de servicios
└── README.md
```

---

## 📚 API Endpoints

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/login` | Iniciar sesión |

### Proyectos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/projects` | Listar proyectos del usuario |
| POST | `/api/projects` | Crear proyecto |
| GET | `/api/projects/{id}` | Obtener proyecto |
| PUT | `/api/projects/{id}` | Actualizar proyecto |
| DELETE | `/api/projects/{id}` | Eliminar proyecto (soft delete) |
| POST | `/api/projects/{id}/activate` | Activar proyecto |

### Tareas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/projects/{projectId}/tasks` | Listar tareas |
| POST | `/api/projects/{projectId}/tasks` | Crear tarea |
| GET | `/api/tasks/{id}` | Obtener tarea |
| PUT | `/api/tasks/{id}` | Actualizar tarea |
| DELETE | `/api/tasks/{id}` | Eliminar tarea (soft delete) |
| POST | `/api/tasks/{id}/complete` | Completar tarea |

---

## 🛠️ Comandos Útiles

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend

# Reiniciar servicios
docker-compose restart

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (reset DB)
docker-compose down -v

# Reconstruir imágenes
docker-compose up -d --build
```

---

## 📄 Licencia

Este proyecto fue desarrollado como parte del assessment de empleabilidad de Riwi.
