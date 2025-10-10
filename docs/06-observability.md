# 06 – Observability (Medium Priority)

Make the service introspectable in dev and prod.

## Findings

- `spring-boot-starter-actuator` present. No explicit management configs in `application.yml` beyond defaults.
- No logging config file present (e.g., `logback-spring.xml`).
- No metrics exporter configured (e.g., Micrometer Prometheus).

## Tasks

- [ ] Actuator configuration by profile in `application-*.yml`:
  - Expose endpoints: dev (`health,info,metrics,prometheus,env,configprops`), prod (`health`, optionally `prometheus`).
  - Add `management.server.port` and `management.endpoints.web.base-path=/actuator`.
  - Configure `health.probes.enabled=true` for readiness/liveness.
- [ ] Metrics & tracing
  - Add `micrometer-registry-prometheus` and enable `/actuator/prometheus` in dev/prod as needed.
  - Optionally add OpenTelemetry instrumentation with OTLP exporter for traces (env-configurable).
- [ ] Structured logging
  - Add `logback-spring.xml` with JSON layout in prod and console pattern in dev.
  - Include request IDs and MDC (e.g., `X-Request-Id`) in logs.
- [ ] Access logs
  - Enable Tomcat/Undertow access logs or Spring `server.tomcat.accesslog.enabled=true` in prod.

## Acceptance Criteria

- `GET /actuator/health` reflects readiness/liveness and is used by Docker healthcheck.
- `/actuator/prometheus` exposes metrics when enabled; dashboards can be built downstream.
- Logs include correlation IDs and are parseable (JSON in prod).
