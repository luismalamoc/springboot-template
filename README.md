# Spring Boot Template Project

This is a template project for Spring Boot applications with a RESTful API structure. The project includes a sample `/tasks` resource demonstrating best practices for Java and Spring Boot development.

## Technologies Used

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- Spring Security with CORS support
- WebClient for API calls
- Maven
- Lombok
- SpringDoc OpenAPI (Swagger)

## Project Structure

```
src/main/java/com/example/springboottemplate/
├── controller/          # REST controllers
├── service/             # Business logic
├── repository/          # Data access layer
├── model/               # JPA entities
├── dto/                 # Data Transfer Objects
├── exception/           # Custom exceptions and error handling
├── config/              # Configuration classes
├── client/              # API client classes for external services
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Build the project (optional but recommended):

```bash
mvn clean install
```

4. Run the application using Maven:

```bash
mvn spring-boot:run
```

The application will start on port 8080 by default.

## API Documentation

Once the application is running, you can access:

- Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

- API docs (OpenAPI JSON) at:
```
http://localhost:8080/v3/api-docs
```

## Sample API Endpoints

The template includes a complete CRUD API for a `Task` resource:

- `GET /tasks` - Get all tasks (with pagination)
- `GET /tasks/{id}` - Get a specific task by ID
- `POST /tasks` - Create a new task
- `PATCH /tasks/{id}` - Update an existing task
- `DELETE /tasks/{id}` - Delete a task

## Database

The application uses an H2 in-memory database by default. You can access the H2 console at:

```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: `password`

## Development Guidelines

This template follows Spring Boot and Java best practices:

1. Clear separation of concerns (controller, service, repository layers)
2. DTO pattern for request/response objects with Lombok
3. Proper exception handling
4. API documentation with OpenAPI
5. Validation using Jakarta Validation
6. Lombok for reducing boilerplate code
7. Transactional management
8. Secure API with Spring Security and CORS configuration
9. Robust WebClient configuration for external API calls

## Extending the Template

To add new features or resources:

1. Create entity models in the `model` package
2. Create DTOs in the `dto` package using Lombok annotations
3. Create repositories in the `repository` package
4. Create service interfaces and implementations in the `service` package
5. Create controllers in the `controller` package
6. For external API integration, use the WebClient configuration in the `client` package

Follow the existing `Task` implementation as a reference.

## WebClient Configuration

The template includes a robust WebClient configuration for making API calls to external services:

- Configurable timeouts and retry mechanisms
- Comprehensive error handling
- Automatic retry for transient errors
- Exponential backoff with jitter

Example properties in `application.properties`:
```properties
# WebClient Configuration
webclient.timeout.connect=60000
webclient.timeout.response=120
webclient.timeout.read=120
webclient.timeout.write=60
webclient.retry.max=3
webclient.retry.initialBackoff=2
webclient.retry.maxBackoff=30
webclient.maxInMemorySize=2
```

## Security Configuration

The template includes a basic Spring Security configuration with CORS support:

- Configurable CORS origins
- Public endpoints for Swagger UI and H2 console
- Stateless session management

CORS configuration in `application.properties`:
```properties
# CORS Configuration
cors.allowed.origins=http://localhost:3000,http://localhost:8080,https://yourdomain.com
```

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.