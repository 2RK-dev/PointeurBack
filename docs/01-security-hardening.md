# 01 – Security Hardening (High Priority)

This project exposes APIs and uses JWT. Tighten defaults and remove risky settings before deployment.

## Findings

- `src/main/resources/application.yml` defaults to `prod` profile but reads secrets from environment variables (`SPRING_DB_*`, `SPRING_JWT_SECRET_KEY`, `SPRING_JWT_EXPIRATION`). No defaults – good – but ensure non-empty in prod.
- `server.servlet.context-path: /api/v1` set globally – OK.
- `spring.h2` is present in `build.gradle` as `runtimeOnly 'com.h2database:h2'`, risking accidental inclusion in prod.
- `spring-boot-devtools` included as `developmentOnly` – OK, but ensure it’s not packaged.
- `actuator` included but no management exposure configuration exists.
- `Dockerfile` uses JDK base image and runs as root, no healthcheck.
- `.env` exists in repo; ensure secrets not committed. `.gitignore` present, but verify it excludes `.env`.

## Tasks

- [ ] Add strict Actuator exposure in `application.yml` per profile.
  - Dev: expose `health`, `info`, `metrics`, `prometheus`.
  - Prod: expose only `health` (readiness/liveness) and optionally `prometheus` behind auth or internal network.
- [ ] Add `management.server.port` and `management.endpoints.web.base-path` for clarity.
- [ ] Validate required env variables at startup (fail-fast): DB url/user/pass, JWT secret, expiration.
- [ ] Move H2 to `testImplementation` and guard dev profile only.
- [ ] Configure CORS if the frontend is separate (add a `CorsConfig`).
- [ ] Enforce JWT alg/length and rotation strategy; document key rotation.
- [ ] Set `server.error.include-*` to minimal in prod (currently always).
- [ ] Add `@Validated` and `@ControllerAdvice` checks for security-related validations.
- [ ] In `Dockerfile`: switch to non-root user, use JRE image, and add `HEALTHCHECK`.
- [ ] In `docker-compose.yml`: restrict service to internal network and add healthchecks; do not publish DB to host by default.

## Acceptance Criteria

- Actuator endpoints limited and documented by profile; `GET /actuator` shows only expected endpoints.
- App fails fast on missing critical env vars in prod.
- No H2 present on prod classpath/JAR; dev tests still pass.
- Container runs as non-root, `docker ps` shows healthy status via healthcheck.
- Database not exposed publicly by default; credentials not present in git.
