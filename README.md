# user-service

A small Spring Boot REST service that manages users. This README documents the framework, architecture, endpoints, configuration, how to build/run the project, and findings / recommended improvements.

## Summary

- A simple CRUD service for User entities.
- Exposes REST endpoints under `/api/users` to create, read, update, delete and list users (paged).
- Uses an external time service (https://worldtimeapi.org) to populate `createdAt` (Asia/Kolkata timezone) and falls back to server time if the external call fails.
- In-memory H2 database for persistence (development).

## Frameworks & Libraries (from pom.xml)

- Java: 17
- Spring Boot: 3.3.0 (parent)
- spring-boot-starter-web
- spring-boot-starter-webflux (WebClient)
- spring-boot-starter-data-jpa
- h2 (runtime)
- spring-boot-starter-validation
- springdoc-openapi-starter-webmvc-ui 2.6.0 (Swagger / OpenAPI UI)
- spring-boot-starter-test (test scope)
- io.rest-assured:rest-assured:5.3.0 (test scope)

## Project structure / packages

- `com.example.userservice`
  - `controller` — REST controllers (HTTP endpoints). Example: `UserController`.
  - `service` — `UserService` interface and `service.impl.UserServiceImpl` implementation.
  - `repository` — JPA repositories (`UserRepository`).
  - `model` — JPA entities (`User`).
  - `dto` — request/response DTOs (`UserRequest`, `UpdateUserRequest`, `UserResponse`, `WorldTimeResponse`).
  - `config` — framework beans (`WebClientConfig`).
  - `exception` — custom exceptions & handlers (`UserNotFoundException`, `GlobalExceptionHandler`).
  - `UserServiceApplication` — application entry point.

## Key classes & responsibilities

- `UserController` — maps endpoints under `/api/users` and delegates to `UserService`.
- `UserService` / `UserServiceImpl` — business logic, interacts with `UserRepository`, and uses `WebClient` to fetch world time.
- `UserRepository` — extends `JpaRepository<User, Long>` and provides `findByUsername(...)`.
- `User` — JPA entity with fields: `id: Long`, `username: String`, `lastName: String`, `age: Integer`, `createdAt: OffsetDateTime`.
- DTOs:
  - `UserRequest` — POST body required fields: `id`, `username`, `lastName`, `age`.
  - `UpdateUserRequest` — PUT body with optional fields to partially update a user.
  - `UserResponse` — returned resource representation including `createdAt`.
  - `WorldTimeResponse` — maps `datetime` from worldtimeapi response.
- `WebClientConfig` — exposes a `WebClient` bean configured with `time.worldtimeapi.url` from `application.yml`.
- `GlobalExceptionHandler` — maps `UserNotFoundException`, validation errors and general exceptions to JSON responses.

## Endpoints (HTTP)

Base path: `/api/users`

- POST `/api/users`
  - Description: Create a new user.
  - Request body (JSON):
    - id: Long (required) — note: ID must be supplied by client in current model.
    - username: String (required)
    - lastName: String (required)
    - age: Integer (required, >= 0)
  - Response: 201 Created — `UserResponse` (includes `createdAt` populated from worldtimeapi or fallback).

- GET `/api/users/{id}`
  - Description: Get user by id.
  - Response: 200 OK — `UserResponse`. 404 Not Found if user missing.

- GET `/api/users/username/{username}`
  - Description: Get user by username.
  - Response: 200 OK — `UserResponse`.

- GET `/api/users` (paged)
  - Description: List users using Spring Data `Pageable` (defaults: page=0, size=10).
  - Response: 200 OK — `Page<UserResponse>` JSON with page metadata and `content` array.

- PUT `/api/users/{id}`
  - Description: Partial update of a user. Only non-null fields in `UpdateUserRequest` are applied.
  - Request body (JSON): `username?`, `lastName?`, `age?` (age must be >= 0 if present).
  - Response: 200 OK — `UserResponse`.
  - Note: `createdAt` is updated to the time fetched at update time in current implementation (consider changing to `updatedAt`).

- DELETE `/api/users/{id}`
  - Description: Delete a user.
  - Response: 204 No Content. 404 Not Found if user missing.

## Configuration (application.yml)

Key properties (src/main/resources/application.yml):

- server.port: 8080
- spring.datasource: H2 in-memory `jdbc:h2:mem:usersdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- spring.jpa.hibernate.ddl-auto: update
- spring.h2.console.enabled: true (path `/h2-console`) — useful for development only.
- time.worldtimeapi.url: `https://worldtimeapi.org/api/timezone/Asia/Kolkata`
- WebClient timeouts: `connect-timeout-ms` and `read-timeout-ms` are present in config and respected by `WebClient` usage pattern if wiring additional timeout settings.

You can override properties on the command line, e.g. `--server.port=9090` or `--time.worldtimeapi.url=<url>`.

## Build & run

Requirements: Java 17, Maven

- Build: mvn clean package
- Run (Maven): mvn spring-boot:run
- Run (JAR): java -jar target/user-service-0.0.1-SNAPSHOT.jar

Swagger UI (OpenAPI):
- http://localhost:8080/swagger-ui/index.html or http://localhost:8080/swagger-ui.html

H2 console:
- http://localhost:8080/h2-console
  - JDBC URL: jdbc:h2:mem:usersdb
  - User: sa
  - Password: (empty)

## Tests

- Run unit/integration tests (none included currently): mvn test
- Recommendation: add unit tests for `UserServiceImpl` and controller integration tests (MockMvc or WebTestClient).

## RestAssured tests

This project now includes RestAssured-based controller tests that validate the `POST /api/users` (create) and `GET /api/users/username/{username}` endpoints.

- Test class: `src/test/java/com/example/userservice/RestAssuredUserApiTest.java`
- Dependency: RestAssured added to `pom.xml` (test scope) — `io.rest-assured:rest-assured:5.3.0`.
- Test approach:
  - The tests run with `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` so the controller is exercised over HTTP.
  - `@MockBean UserService` is used to stub the service layer; this isolates controller behavior (request mapping, validation, response shape) and avoids external calls or DB setup.
  - Tests use RestAssured to send HTTP requests and assert status codes and JSON response fields.

How to run the tests locally

- Run the single test class (recommended while developing):

```bash
mvn -Dtest=RestAssuredUserApiTest test
```

- Run all tests:

```bash
mvn test
```

Notes

- Because RestAssured is test-scoped, its classes are available only to test code under `src/test/java`.
- If your IDE reports "cannot resolve symbol" for RestAssured, refresh/reimport the Maven project and ensure the project SDK is set to Java 17. See the Troubleshooting section above for more detail.

Committing and pushing these changes

- To commit locally and push to GitHub (if you have remote set up and authentication configured), run:

```bash
# stage files (README, pom, tests)
git add README.md pom.xml src/test/java/com/example/userservice/RestAssuredUserApiTest.java

# commit
git commit -m "docs: add RestAssured test instructions and tests for create/get user endpoints"

# push to remote (default branch)
git push
```

- If `git push` fails due to authentication or no remote configured, you'll see an error message — please share it and I can help format the correct remote/push command or provide SSH/HTTPS setup instructions.


## Findings, notes & recommended improvements

Findings (current behavior):

- The `User` entity uses client-provided `id` (no @GeneratedValue). This requires every client to supply a unique id and may lead to conflicts.
- `createdAt` is set on create and overwritten on update. Consider adding `createdAt` (immutable) and an `updatedAt` field instead.
- External time fetch uses a synchronous `.block()` on a `WebClient`. This is simple and works for small scale but may block reactor threads or servlet threads under high load. Consider using non-blocking/reactive patterns or a resilient approach.
- No unique constraint on `username` at DB level (duplicates allowed).
- H2 console is enabled by default — fine for local development but must be disabled or secured for production.
- No authentication/authorization present in the project.
- No automated tests are present in the repository.

Recommended improvements (low-risk first):

- Add `@GeneratedValue` to `User.id` and make `id` optional in `UserRequest` (or remove `id` from POST payload).
- Preserve `createdAt` and add `updatedAt` for updates.
- Add unique constraint on `username` and handle duplicate key exceptions gracefully.
- Add simple unit tests and an integration test suite.
- Replace blocking WebClient usage with non-blocking handling or move external fetch to a small cache or scheduled task to avoid per-request external calls.
- Use Flyway or Liquibase for DB migrations in non-dev environments.
- Disable H2 console and secure DB credentials in production profiles.
- Add logging and observability (metrics, health checks) and consider rate limiting for public endpoints.

## Useful commands & troubleshooting

- Build: mvn clean package
- Run (dev): mvn spring-boot:run
- Run jar: java -jar target/user-service-0.0.1-SNAPSHOT.jar
- Change port: append `--server.port=9090` to any run command.
- Override time API URL: `--time.worldtimeapi.url=<url>`

Troubleshooting tips

- If the server fails to start due to port conflict, change the port.
- If calls to worldtimeapi fail, the service falls back to server time (Asia/Kolkata). Check logs for timeout/exception details.
- Validation failures return HTTP 400 with a JSON map of field -> message produced by `GlobalExceptionHandler`.

## Next steps & options

If you want, I can:

- Add a set of example curl commands for the main flows (create, get, update, delete).
- Add basic unit tests for `UserServiceImpl` and an integration test for `UserController`.
- Add OpenAPI annotations or examples to enhance Swagger UI.

---

README generated/updated to describe the codebase, runtime, endpoints and recommended improvements.
