# 08 – Developer Experience & Housekeeping (Low Priority)

Improve day-to-day productivity and consistency.

## Findings

- `.gitignore` excludes `**/.env` – good. `.env` and `.env.example` exist. Ensure secrets are not committed.
- No `.editorconfig` or format enforcement detected. No `.dockerignore` present.
- Basic `README.md` present; lacks environment var catalog and contribution guide.

## Tasks

- [ ] Add `.editorconfig` for consistent indentation, charset, EOLs across IDEs.
- [ ] Add code format and lint tools: `spotless` with Google Java Format; `checkstyle` or `pmd`.
- [ ] Add pre-commit hooks: run `spotlessApply` and basic checks; optionally use Husky-like flow or pre-commit framework.
- [ ] Add `.dockerignore` to speed builds and reduce context.
- [ ] Provide `Makefile` or Gradle tasks aliases for common flows: build, test, run, docker-build, docker-up/down.
- [ ] README: expand with env var table, developer onboarding, and common scripts.
- [ ] Sample HTTP requests under `docs/examples/` for quick testing.

## Acceptance Criteria

- Consistent formatting enforced; pre-commit hooks prevent style regressions.
- Faster Docker builds with minimal context.
- New contributors can set up and run the project in <10 minutes using the README.
