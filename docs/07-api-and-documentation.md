# 07 – API & Documentation (Medium Priority)

Make the API self-describing and developer-friendly.

## Findings

- Multiple controllers exist (`src/main/java/.../controller/*Controller.java`). No OpenAPI/Swagger dependency detected.
- `README.md` has high-level instructions but lacks API usage, env var catalog, and troubleshooting.

## Tasks

- [ ] Add OpenAPI documentation
  - Include `org.springdoc:springdoc-openapi-starter-webmvc-ui` (for Spring Boot 3).
  - Configure OpenAPI group and UI at `/swagger-ui.html` and spec at `/v3/api-docs`.
  - Annotate controllers/DTOs with operation summaries, parameter descriptions, and response schemas.
- [ ] Error model
  - Document error envelope (`ErrorDetails`, `ValidationErrorDetails`) and status codes from `GlobalExceptionHandler`.
- [ ] Versioning strategy
  - API already prefixed with `/api/v1` in `application.yml`. Document versioning policy and deprecation process.
- [ ] README enhancements
  - Add "Configuration" section listing required env vars with descriptions and defaults from `.env.example`.
  - Add "Running locally" (dev profile), "Running tests", and "Docker" sections with common commands.
  - Add "Troubleshooting" with common DB and migration issues and healthcheck endpoints.
- [ ] Postman/HTTP examples
  - Provide a Postman collection or `.http` files under `docs/examples/` for key endpoints (CRUD for groups, levels, teachers, rooms, schedules, import/export).

## Acceptance Criteria

- OpenAPI UI accessible locally and in non-prod environments; spec is valid.
- Every public endpoint has operation docs and example requests/responses.
- README contains clear setup, env var table, and troubleshooting.
- Example requests provided and verified against running app.
