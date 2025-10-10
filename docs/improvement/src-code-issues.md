# Source Code Issues – Prioritized TODOs

This document lists code-level warnings/errors found under `src/`, grouped by priority. Each item includes the reason/impact and an actionable fix with acceptance criteria.

## High Priority

- [ ] Logic bug in group creation uniqueness check
  - Location: `src/main/java/io/github/two_rk_dev/pointeurback/service/implementation/LevelServiceImpl.java` → `createGroup(...)` lines ~152–165
  - Problem: The uniqueness check appears inverted and uses the wrong exception.
    - Current:
      ```java
      if (groupRepository.findByName(dto.name()) != null) {
          Group savedGroup = groupRepository.save(newGroup);
          return groupMapper.toDto(savedGroup);
      } else {
          throw new GroupNotFoundException("Group name already exists for this level");
      }
      ```
    - If the name exists, it saves; otherwise throws "already exists" — contradictory. Also `GroupNotFoundException` is the wrong semantic.
  - Impact: Allows duplicates or blocks valid inserts; inconsistent error semantics for clients.
  - Fix:
    - Check for existence and throw a dedicated conflict (e.g., `GroupNameNotUniqueException`) when found; only save when not found.
    - Add repository method that validates uniqueness per level if needed.
  - Acceptance:
    - Creating a group with an existing name for the same level returns HTTP 409 with a clear error code; creating a unique name succeeds.

- [ ] Potential NullPointerException in schedule conflict checks
  - Location: `src/main/java/io/github/two_rk_dev/pointeurback/service/implementation/ScheduleServiceImpl.java`
    - `updateScheduleItem(...)` lines ~63–85 uses `existingItem.getTeacher().getId()` without null guard.
    - `addScheduleItem(...)` lines ~109–118 uses `newItem.getTeacher().getId()` without null guard.
  - Impact: If teacher is optional or missing in DTO, conflict check will throw NPE, returning 500.
  - Fix:
    - Use `Optional.ofNullable(...).map(Teacher::getId).orElse(null)` consistently for teacher and room IDs when querying conflicts.
    - Validate required associations up-front; if teacher is mandatory, enforce via validation and fail with 400.
  - Acceptance:
    - No NPEs during schedule create/update when teacher/room are absent; appropriate 4xx validation errors instead.

- [ ] Unimplemented endpoint behavior
  - Location: `ScheduleServiceImpl.getScheduleById(Long)` returns `null` (lines ~124–127)
  - Impact: Any controller calling this will produce 500 or 204 incorrectly.
  - Fix:
    - Implement: `findById(...).orElseThrow(new ScheduleItemNotFoundException(...))` and map to DTO.
  - Acceptance:
    - Getting a schedule by ID returns 200 with DTO or 404 via `ScheduleItemNotFoundException` handled by `GlobalExceptionHandler`.

- [ ] Overly broad throws in controller
  - Location: `src/main/java/io/github/two_rk_dev/pointeurback/controller/ExportController.java` → `export(...)` declares `throws Exception`
  - Impact: Leaks unchecked exceptions as 500; hinders precise error handling and API docs.
  - Fix:
    - Remove `throws Exception`; handle invalid inputs with `IllegalArgumentException` (400) or domain-specific exceptions mapped in `GlobalExceptionHandler`.
  - Acceptance:
    - Invalid format or params return 400/422 with a documented error payload; no raw stack traces.

- [ ] Missing transactional boundary on destructive aggregate operations
  - Location: `LevelServiceImpl.deleteLevel(...)`
  - Problem: Removes schedule relationships and clears groups, then deletes the level without `@Transactional`.
  - Impact: Risk of partial updates, lazy-loading issues, or constraint violations under failure.
  - Fix:
    - Annotate with `@Transactional`; ensure orphan removal/cascade rules are correct on entities.
  - Acceptance:
    - Deleting a level is atomic; database remains consistent under failures.

- [ ] Incorrect level/group validation in schedule retrieval
  - Location: `ScheduleServiceImpl.getSchedule(...)` (lines ~47–55)
  - Problem: When `groupId` is provided with `levelId`, it checks `groupRepository.existsGroupByLevel_IdIs(levelId)` which doesn't validate that the specific `groupId` belongs to the given `levelId`.
  - Impact: Users can query mismatched level/group pairs without proper validation, or get incorrect 404s.
  - Fix:
    - Validate with a repository method like `existsByIdAndLevel_Id(groupId, levelId)`.
  - Acceptance:
    - Requests with mismatched level/group return 404 with clear error code; valid pairs return 200 with items.

## Medium Priority

- [ ] Map common service exceptions to appropriate HTTP codes
  - Location: `src/main/java/io/github/two_rk_dev/pointeurback/exception/GlobalExceptionHandler.java`
  - Problem: `@ExceptionHandler(Exception.class)` returns 500 with `ex.getMessage()`. `IllegalArgumentException` and `IllegalStateException` from services fall into 500 instead of 400/409.
  - Impact: Poor API ergonomics; client cannot distinguish validation vs server errors.
  - Fix:
    - Add handlers mapping `IllegalArgumentException` → 400 and `IllegalStateException` (e.g., conflicts) → 409, with consistent error codes.
    - Avoid echoing raw messages in prod.
  - Acceptance:
    - Service validation errors yield 400; conflicts yield 409; generic 500 no longer leaks internal messages in prod.

- [ ] Transactional read on `getAll()`
  - Location: `LevelServiceImpl.getAll()` has `@Transactional`.
  - Note: Not harmful, but unnecessary for simple reads; prefer `@Transactional(readOnly = true)` if transaction is needed for lazy loading.
  - Fix: Remove or change to `readOnly = true`.

- [ ] Controller-service coupling by implementation type
  - Location: Controllers inject `...service.implementation.*Impl` directly (e.g., `LevelController`, `GroupController`).
  - Impact: Reduces testability and violates interface-driven design.
  - Fix: Inject the service interface (e.g., `LevelService`) instead of the concrete implementation.

- [ ] Error messages and exception types consistency
  - Location: Various services (e.g., `LevelServiceImpl`)
  - Problem: Using `GroupNotFoundException` to indicate uniqueness conflict; messages may mismatch.
  - Fix: Introduce specific exceptions for uniqueness (e.g., `GroupNameNotUniqueException`) and reuse in handler with 409.

## Low Priority

- [ ] Minor style: double space in class declaration
  - Location: `src/main/java/io/github/two_rk_dev/pointeurback/controller/GroupController.java` line ~12: `public class  GroupController` (two spaces)
  - Impact: Style nit only.
  - Fix: Normalize whitespace.

- [ ] Prefer constructor parameter annotations for clarity
  - Location: Various services/constructors.
  - Note: Not required, but consider `@NonNull` annotations for parameters to make contract explicit.

## Suggested Next Steps

- **Patch high-priority items first** (logic bug, NPE risks, unimplemented method, controller throws, transactional boundary, validation correctness).
- **Then medium** (exception mapping, read-only transactions, interface injection).
- **Finally low** (style and nits).

## Verification Checklist

- Add/extend tests to cover group creation uniqueness, schedule conflict without teacher/room, and `getScheduleById`.
- Verify status codes via controller tests and integration tests with Testcontainers.
- Run static analysis (SpotBugs/PMD) to catch remaining nullability and API misuse.
