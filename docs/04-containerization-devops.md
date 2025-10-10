# 04 – Containerization & DevOps (High Priority)

Reliable, secure runtime images and basic automation for CI/CD.

## Findings

- `Dockerfile` builds with `gradle:8.8-jdk21-alpine` then runs on `eclipse-temurin:21-jdk-alpine` as root; no healthcheck.
- `docker-compose.yml` maps `${APP_PORT}` and exposes Postgres to host `${POSTGRES_PORT}:5432`; uses named volume `db_data`.
- No `.dockerignore` detected; builds may be slow and leak context.

## Tasks

- [ ] Dockerfile improvements:
  - Use JRE runtime image (`eclipse-temurin:21-jre-alpine`) to reduce size.
  - Create non-root user and run the jar under it.
  - Add `HEALTHCHECK` hitting `/actuator/health`.
  - Leverage build cache by copying `build.gradle`, `settings.gradle`, `gradle/` first, run `gradle dependencies`, then copy sources.
  - Optionally produce layered jar (`spring-boot` layers) and use `java -Djarmode=layertools` or `--layers`.
- [ ] Add `.dockerignore` to exclude `.git`, `build/`, `bin/`, IDE files.
- [ ] docker-compose hardening:
  - Remove host port exposure for Postgres in default profile; use internal network.
  - Add `healthcheck` for `postgres` and `app`, and `depends_on` health-conditions.
  - Use environment variables with defaults in `.env.example` and document.
- [ ] CI pipeline (skeleton):
  - Build: `./gradlew build`.
  - Test: unit + integration (with Testcontainers).
  - Security: `trivy` scan for image, `dependency-check` or Snyk.
  - Publish container to registry (optional).

## Acceptance Criteria

- Container image runs as non-root, smaller size, and exposes a working health endpoint.
- Compose brings up healthy services without exposing Postgres to host by default.
- CI builds, tests, and (optionally) pushes an image; security scan report produced.
