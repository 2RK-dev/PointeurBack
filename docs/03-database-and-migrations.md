# 03 – Database & Migrations (High Priority)

Harden persistence: consistent environments, reliable migrations, and safe local workflows.

## Findings

- `application.yml` uses env vars for datasource and Flyway. `baseline-on-migrate: true`. `locations: classpath:db/migration`.
- Migrations folder `src/main/resources/db/migration/` contains `V1__Initial_schema.sql` only.
- `docker-compose.yml` provisions `postgres:16` with bind port `${POSTGRES_PORT}:5432` and persistent volume `db_data`.
- H2 is on runtime classpath; better limit to tests.

## Tasks

- [ ] Profiles & config split:
  - Add `application-dev.yml`, `application-test.yml`, `application-prod.yml` with profile-specific DB URLs and Flyway toggles.
  - In dev: optional H2 or local Postgres; in test: Testcontainers; in prod: Postgres with SSL if applicable.
- [ ] Flyway strategy:
  - Introduce repeatable migrations `R__*.sql` for views/functions/seeds.
  - Disallow `baseline-on-migrate` in new projects unless upgrading legacy DB. Document usage if kept.
  - Add naming conventions and review checklist for migrations.
- [ ] Test strategy:
  - Use `org.testcontainers:postgresql` for integration tests. Auto-apply Flyway before tests.
  - Seed minimal reference data via `R__seed.sql`.
- [ ] Data integrity:
  - Ensure constraints, indexes, and FKs present in `V1__Initial_schema.sql` (review and add as needed).
  - Add uniqueness constraints mirrored by application-level validations.
- [ ] Connection pool:
  - Configure HikariCP settings per profile (pool size, timeouts).
- [ ] Backups & recovery (ops):
  - In Compose, add periodic dump job (optional) or document backup process.

## Acceptance Criteria

- Separate profile YAMLs exist; `SPRING_PROFILES_ACTIVE` selects correct datasource and Flyway settings.
- `./gradlew test` runs with Testcontainers Postgres and applies Flyway migrations automatically.
- Repeatable migrations supported and executed; initial schema validated.
- Unique constraints and indexes verified on key tables.
- H2 used in tests only; no H2 on production runtime classpath.
