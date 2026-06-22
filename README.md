# Pet Care Scheduler (Critter Chronologer)

Pet Care Scheduler is a Spring Boot REST API for managing a pet-care business. It keeps track of customers, pets, employees, employee availability, employee skills, and daily service schedules so the right caregivers can be matched to the right pets at the right time.

This project is based on Udacity's Critter Chronologer concept and is implemented with Spring Boot, Spring Web, Spring Data JPA, MySQL for local runtime storage, and H2 for tests.

## What I found while inspecting the project

This is an exciting little domain model because it already has the core pieces of a real pet-care scheduling system:

- **Customers own pets.** Customer records include names, phone numbers, notes, and an ordered list of pets.
- **Pets belong to customers.** Pet records capture a type, name, owner, birth date, and notes.
- **Employees have skills and availability.** Employees can be matched by service skills such as walking, feeding, medicating, shaving, and petting.
- **Schedules connect everything.** A schedule can include multiple pets, multiple employees, a date, and the activities to perform.
- **The API is layered cleanly.** Controllers receive HTTP requests, services hold business logic, repositories handle persistence, and DTOs shape request/response payloads.
- **The functional tests document the expected behavior.** They cover saving customers, saving pets, employee availability matching, schedule creation, and schedule lookup by employee, pet, and customer.

## Current capabilities

### Customer management

- Create a customer.
- List all customers.
- Find the owner of a pet.
- Return customer responses with associated pet IDs.

### Pet management

- Create a pet and attach it to an existing owner.
- Fetch a single pet.
- List all pets.
- List pets by owner.

### Employee management

- Create employees with skills.
- Fetch an employee.
- Set weekly availability.
- Search for employees who are available on a requested date and have all requested skills.

### Schedule management

- Create schedules for pets, employees, service date, and activities.
- List all schedules.
- Find schedules by pet.
- Find schedules by employee.
- Find schedules by customer through the customer's pets.

## Tech stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.3.5 |
| API layer | Spring Web |
| Persistence | Spring Data JPA / Hibernate |
| Runtime database | MySQL |
| Test database | H2 in-memory database |
| Build tool | Maven |
| Test framework | Spring Boot Test / JUnit 5 |

## Project structure

```text
src/main/java/com/udacity/jdnd/course3/critter
├── CritterApplication.java        # Spring Boot entry point
├── CritterController.java         # Simple /test health-style endpoint
├── pet                            # Pet entity, DTO, repository, controller
├── schedule                       # Schedule entity, DTO, repository, controller
├── service                        # Business logic for pets, users, schedules
└── user                           # Customer/employee entities, DTOs, repositories, controller
```

## API reference

The application runs on port `8082` by default.

### Health check

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/test` | Confirms the starter endpoint is reachable. |

### Customers and employees

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/user/customer` | Create a customer. |
| `GET` | `/user/customer` | List all customers. |
| `GET` | `/user/customer/pet/{petId}` | Find the customer who owns a pet. |
| `POST` | `/user/employee` | Create an employee. |
| `POST` | `/user/employee/{employeeId}` | Fetch an employee by ID. |
| `PUT` | `/user/employee/{employeeId}` | Set an employee's available days. |
| `GET` | `/user/employee/availability` | Find employees by requested date and skills. |

> Note: `POST /user/employee/{employeeId}` and `GET /user/employee/availability` with a request body match the current implementation and test expectations, but they are not ideal REST conventions. See [Recommended improvements](#recommended-improvements).

### Pets

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/pet` | Create a pet for an existing owner. |
| `GET` | `/pet/{petId}` | Fetch a pet by ID. |
| `GET` | `/pet` | List all pets. |
| `GET` | `/pet/owner/{ownerId}` | List pets owned by a customer. |

### Schedules

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/schedule` | Create a care schedule. |
| `GET` | `/schedule` | List all schedules. |
| `GET` | `/schedule/pet/{petId}` | List schedules for a pet. |
| `GET` | `/schedule/employee/{employeeId}` | List schedules for an employee. |
| `GET` | `/schedule/customer/{customerId}` | List schedules for a customer. |

## Example requests

### Create a customer

```bash
curl -X POST http://localhost:8082/user/customer \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Ada Lovelace",
    "phoneNumber": "555-0100",
    "notes": "Prefers morning appointments"
  }'
```

### Create a pet

```bash
curl -X POST http://localhost:8082/pet \
  -H 'Content-Type: application/json' \
  -d '{
    "type": "DOG",
    "name": "Pixel",
    "ownerId": 1,
    "birthDate": "2021-05-14",
    "notes": "Loves long walks"
  }'
```

### Create an employee

```bash
curl -X POST http://localhost:8082/user/employee \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Sam Carter",
    "skills": ["WALKING", "FEEDING", "PETTING"],
    "daysAvailable": ["MONDAY", "WEDNESDAY", "FRIDAY"]
  }'
```

### Create a schedule

```bash
curl -X POST http://localhost:8082/schedule \
  -H 'Content-Type: application/json' \
  -d '{
    "employeeIds": [1],
    "petIds": [1],
    "date": "2026-06-12",
    "activities": ["WALKING", "FEEDING"]
  }'
```

## Getting started

### Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8+ for running the app locally

### Database setup

The default runtime configuration expects a MySQL database reachable at `localhost:3306` with these credentials:

```properties
MYSQL_DB=critter
MYSQL_USER=critter
MYSQL_PASSWORD=critter
```

You can either create that user/database manually or override the values with environment variables:

```bash
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DB=critter
export MYSQL_USER=critter
export MYSQL_PASSWORD=critter
```

### Run the application

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8082/test
```

Expected response:

```text
Critter Starter installed successfully
```

### Run tests

```bash
mvn test
```

Tests use the H2 in-memory configuration in `src/test/resources/application.properties`, so they do not require MySQL.

## Configuration

Runtime configuration lives in `src/main/resources/application.properties`.

Important defaults:

```properties
server.port=8082
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DB:critter}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${MYSQL_USER:critter}
spring.datasource.password=${MYSQL_PASSWORD:critter}
spring.jpa.hibernate.ddl-auto=update
```

Test configuration lives in `src/test/resources/application.properties` and uses H2 with `ddl-auto=create-drop`.

## Recommended improvements

Here are the highest-value improvements I would make next:

1. **Add request validation.** Add Bean Validation annotations such as `@NotBlank`, `@NotNull`, `@NotEmpty`, and `@FutureOrPresent` to DTOs, then enable validation with `@Valid` in controllers.
2. **Normalize REST endpoints.** Change employee lookup from `POST /user/employee/{employeeId}` to `GET /user/employee/{employeeId}` and consider making employee availability search a `POST` endpoint because it uses a JSON request body.
3. **Add global exception handling.** Replace scattered `ResponseStatusException` usage with a `@ControllerAdvice` that returns consistent error payloads.
4. **Protect schedule creation rules.** Validate that employees assigned to a schedule are available on the schedule date and have the requested activities before saving.
5. **Add update and delete workflows.** The API currently focuses on create/read operations; updates and deletes would make it more production-ready.
6. **Improve mapping consistency.** Move entity-to-DTO conversion into dedicated mapper classes or MapStruct to reduce repeated conversion code in services.
7. **Add pagination.** List endpoints currently return all rows; pagination will help as customer, pet, employee, and schedule records grow.
8. **Keep generated artifacts out of version control.** The `target/` directory contains compiled classes and should remain ignored rather than committed.
9. **Add API documentation.** Add OpenAPI/Swagger documentation so consumers can discover request and response schemas interactively.
10. **Add Docker Compose.** A local `docker-compose.yml` for MySQL would make onboarding faster and more repeatable.

## Development notes

- The code uses constructor injection, which is a good Spring practice.
- Repository methods are ordered by ID where test expectations depend on deterministic ordering.
- Entity collections are initialized defensively to avoid null collection issues.
- The Maven configuration now declares H2 once, which avoids duplicate dependency warnings during builds.

## Postman collection

A Udacity Postman collection is included at:

```text
src/main/resources/Udacity.postman_collection.json
```

Import it into Postman to explore the expected API interactions.

## License

No license file is currently included. Add a license before distributing or reusing this project publicly.
