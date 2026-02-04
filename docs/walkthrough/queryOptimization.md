# Walkthrough - N+1 Query Optimization

I have optimized the database queries in the `tenpo-backend` project to prevent N+1 performance issues.

## Changes Made

### 1. Optimized `TransactionRepository`
Added `@EntityGraph` to all methods that fetch `Transaction` entities. This ensures the `Employee` relationship is fetched in the same query.

[TransactionRepository.java](file:///c:/Users/Lenovo/Documents/Proyectos/tenpo/backend/src/main/java/com/tenpo/repository/TransactionRepository.java)
```java
    @EntityGraph(attributePaths = {"employee"})
    @Query("SELECT t FROM Transaction t WHERE t.employee.client.id = :clientId")
    List<Transaction> findByClientId(@Param("clientId") Integer clientId);
```

### 2. Optimized `EmployeeRepository`
Added `@EntityGraph` to `findByRut` to eagerly fetch the associated `Client`.

[EmployeeRepository.java](file:///c:/Users/Lenovo/Documents/Proyectos/tenpo/backend/src/main/java/com/tenpo/repository/EmployeeRepository.java)
```java
    @EntityGraph(attributePaths = {"client"})
    Optional<Employee> findByRut(String rut);
```

### 3. Global Optimization in `application.yml`
Added `default_batch_fetch_size: 20` to Hibernate properties. This acts as a safety net, automatically batching queries for lazy relationships that weren't explicitly optimized with `@EntityGraph`.

[application.yml](file:///c:/Users/Lenovo/Documents/Proyectos/tenpo/backend/src/main/resources/application.yml)
```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 20
```

### 4. Comprehensive Testing Suite
I have implemented a full suite of tests to ensure correctness and prevent regressions:
- **CORS Configuration**: Allowed origin `http://localhost:5173` in `WebConfig.java` to support frontend integration.
- **Unit Tests**: Coverage for all public methods in `ClientService`, `EmployeeService`, and `TransactionService`.
- **Repository Tests**: Integration tests using H2 for `ClientRepository`, `EmployeeRepository`, and `TransactionRepository`.
- **H2 Configuration**: Added `src/test/resources/application.yml` for isolated testing.

## Verification Results

### SQL Query Comparison (Verified)
Using the Hibernate debug logs, I verified that `findByClientId` now executes a single `LEFT JOIN` query instead of multiple sequential selects.

### Test Execution
Successfully ran all **61 tests** (Web, Service, and Repository layers).

```bash
[INFO] Results:
[INFO] Tests run: 61, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
