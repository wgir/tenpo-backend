Buid a java backend api taking as reference the file docs/requirements.md and following the rules defined in docs/rules.md

You are an expert in Java 21, Spring Boot 3, JUnit 5, and Mockito.
Generate a complete and high-quality unit test for the following Spring Boot controller.

Requirements:
1. Use JUnit 5.
2. Use Mockito for mocking services.
3. Use @WebMvcTest for controller testing.
4. Use MockMvc for sending requests and validating responses.
5. Include tests for:
    - Success cases
    - Validation errors
    - Not found
    - Exception handling
6. Mock all service calls that the controller depends on.
7. Validate:
    - HTTP status codes
    - JSON response structure
    - Content type
8. Include Arrange–Act–Assert sections for clarity.
9. Add clear method names following 'shouldXyzWhenAbc' convention.
10. Use ObjectMapper for JSON serialization.
11. Include imports.

Here is the controller:

ClientController

Generate only the test class. Do not rewrite the controller.


-----------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------

You are an expert in Java 21, Spring Boot 3, JUnit 5, and Mockito.
Generate a complete and high-quality UNIT TEST for the following Spring Boot Service class.

Requirements:
1. Use JUnit 5.
2. Use Mockito for mocking dependencies.
3. Use @ExtendWith(MockitoExtension.class).
4. Use @Mock and @InjectMocks annotations.
5. Include Arrange–Act–Assert structure in each test.
6. Test all public methods.
7. Include test cases for:
    - Success scenarios
    - Error or exceptional scenarios
    - Boundary conditions or edge cases
    - Optional empty cases (if applicable)
8. Use clear and descriptive method names using 'shouldXyzWhenAbc' convention.
9. Verify interactions with mocked dependencies using Mockito verify().
10. Ensure no unnecessary integration logic is included.
11. Include full import statements.
12. DO NOT rewrite the service code; only generate the test class.

Here is the service:


ClientService

Generate only the test class.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

You are an expert in Java 21, Spring Boot 3, Hibernate, JPA, and JUnit 5.
Generate a complete and high-quality repository test for the following Spring Data JPA repository.

Requirements:
1. Use @DataJpaTest to load only JPA components.
2. Use JUnit 5.
3. Use H2 or an in-memory database.
4. Autowire:
    - The repository under test
    - TestEntityManager
5. Insert sample records using TestEntityManager for setup.
6. Include tests for:
    - Save operations
    - Find operations (findById, findAll, findByXyz if exists)
    - Update operations
    - Delete operations
    - Query methods (custom queries or JPQL)
    - Edge cases (empty results, not found, constraints)
7. Use AssertJ for assertions.
8. Clear, descriptive test method names:
       shouldXyzWhenAbc
9. Ensure correct transactional behavior.
10. Include all necessary import statements.
11. Do NOT rewrite the repository; only generate the test class.

Here is the repositories:

ClientRepository
EmployeeRepository
TransactionRepository

Generate only the test class.