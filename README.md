# user-service

Simple Spring Boot project exposing two APIs:

- `POST /api/users` - create user (body: id, username, lastName, age)
- `GET /api/users/{id}` - get user by id

Run:
- Java 17 + Maven
- `mvn spring-boot:run`

H2 console: http://localhost:8080/h2-console
Swagger UI: http://localhost:8080/swagger-ui.html
