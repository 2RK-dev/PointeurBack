# 02 – Dependency Alignment (High Priority)

Ensure coherent, secure, and reproducible dependencies.

## Findings

- `build.gradle` uses Spring Boot `3.5.3` with plugin `io.spring.dependency-management`.
- Explicit versions for non-managed libs: `mapstruct:1.6.3`, `jackson-dataformat-csv:2.15.2`, `poi-ooxml:5.2.3`.
  - Spring Boot 3.5 BOM manages Jackson 2.17.x; pinning 2.15.2 risks conflicts.
- `lombok`, `mapstruct-processor`, JetBrains `annotations` as annotation processors are fine; verify scope.
- `h2` included as `runtimeOnly`; should be `testImplementation` only.

## Tasks

- [ ] Rely on Spring Boot BOM: remove explicit versions for dependencies that BOM manages (e.g., Jackson). Keep explicit where not managed (MapStruct) or where you need to override.
- [ ] Move `com.h2database:h2` to `testImplementation`.
- [ ] Add dependency constraints for critical libs if overriding (e.g., MapStruct) and document rationale.
- [ ] Add Gradle wrapper validation and lockfile:
  - Enable dependency locking (`gradle/dependency-locking`), or use `version catalogs` for clarity.
  - Run `./gradlew wrapper --gradle-version <current>` and commit `gradle/wrapper/*`.
- [ ] Add `./gradlew dependencyUpdates` via `com.github.ben-manes.versions` plugin for update reports.
- [ ] Ensure `annotationProcessor` config contains only needed processors and matches `compileOnly` Lombok.

## Acceptance Criteria

- `./gradlew dependencies` shows Jackson aligned with Spring Boot BOM (no 2.15.x leftovers).
- No H2 on runtime classpath; tests still run locally using H2/Testcontainers.
- Dependency update report available and reproducible builds confirmed (locks/catalogs committed).
