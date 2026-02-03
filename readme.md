# Tenpo Backend API Challenge

This is a RESTful API built with Java 21 and Spring Boot 3.4.1 to manage clients, employees, and transactions.

## Tech Stack
- **Java 21**
- **Spring Boot 3.4.1**
- **PostgreSQL 18.1**
- **Flyway** for database migrations
- **Lombok**
- **Swagger** for documentation
- **Docker** & **Docker Compose**

## Requirements & Constraints
- Maximum 100 transactions per client.
- No negative transaction amounts.
- No future transaction dates.
- Rate limiting: 3 requests per minute per client.
- Global error handling with `ProblemDetails`.

## Getting Started

### Prerequisites
- Docker and Docker Compose installed.

### Method 1: Docker Compose (Fully Automated)
This is the easiest way to run everything (API + Database) together.
1.  Run the following command in the project root:
    ```bash
    docker-compose up --build
    ```

    In case you must update the image
    ```bash
    docker-compose down -v
    ```
2.  The API will be available at `http://localhost:8080`.

### Method 2: Maven (Local Run)
Use this if you want to run the application locally but have the database running in Docker.
1.  Start only the database:
    ```bash
    docker-compose up -d db
    ```
2.  Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```

### API Documentation
Once the application is running, you can access:
*   **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
*   **Postman Collection**: Located at `docs/tenpo-api-collection.json`.

## API Endpoints

### Clients
- `GET /client`: Get all clients.
- `POST /client`: Create a new client.
- `GET /client/{id}`: Get a client by ID.
- `PUT /client/{id}`: Update a client.
- `DELETE /client/{id}`: Delete a client.

### Employees
- `GET /employee`: Get all employees.
- `POST /employee`: Create a new employee.
- `GET /employee/{id}`: Get an employee by ID.
- `PUT /employee/{id}`: Update an employee.
- `DELETE /employee/{id}`: Delete an employee.

### Transactions
- `GET /transaction`: Get all transactions.
- `POST /transaction`: Create a new transaction.
- `GET /transaction/{id}`: Get a transaction by ID.
- `PUT /transaction/{id}`: Update a transaction.
- `DELETE /transaction/{id}`: Delete a transaction.

## Project Structure
- `com.tenpo.api`: Controllers and DTOs.
- `com.tenpo.service`: Business logic.
- `com.tenpo.repository`: Data access interfaces.
- `com.tenpo.model`: JPA Entities.
- `com.tenpo.config`: Configuration classes.
- `com.tenpo.interceptor`: Rate limiting logic.
- `com.tenpo.exception`: Global error handling.
