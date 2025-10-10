# 05 – Testing & Quality (Medium Priority)

Raise safety net and maintainability with layered tests and quality gates.

## Findings

- `build.gradle` uses JUnit Platform. No visible testing libraries beyond Spring Boot Starter Test.
- `src/test/java/` exists but not inspected for breadth. No explicit integration testing infra configured.
- H2 present at runtime; better to use Testcontainers for closer prod parity.

## Tasks

- [ ] Unit tests
  - Add tests for mappers (MapStruct), services, and exception handling (`GlobalExceptionHandler`).
  - Use `@WebMvcTest` for controller slice tests with mocked services.
- [ ] Integration tests
  - Add Testcontainers Postgres to `testImplementation`.
  - Start container per test class and apply Flyway automatically.
  - Use `@SpringBootTest(webEnvironment = RANDOM_PORT)` and verify API through `TestRestTemplate`.
- [ ] Validation tests
  - DTO validation tests leveraging `spring-boot-starter-validation` constraints.
- [ ] Static analysis & coverage
  - Add `spotless` for formatting, `checkstyle` or `pmd` for static checks.
  - Add `jacoco` for coverage; set threshold gate (e.g., 70-80%).
- [ ] Test data builders
  - Introduce simple builders/factories for DTO/entity creation in tests.

## Acceptance Criteria

- CI runs unit and integration tests. Coverage report generated with threshold gate.
- Controller, service, mapper, and validation tests exist with meaningful assertions.
- Integration tests run on Postgres via Testcontainers and pass locally and in CI.
