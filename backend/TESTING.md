# Testing Guide - Project & Task Management API

## Overview

This project includes unit tests covering critical business use cases using JUnit 5 and Mockito, without loading Spring context.

## Test Structure

```
src/test/java/com/riwi/assesment/
├── application/
│   └── service/                    # Service/Use Case tests
│       ├── ActivateProjectServiceTest.java  (6 tests)
│       └── CompleteTaskServiceTest.java     (7 tests)
└── AssesmentApplicationTests.java           # Spring Boot context test
```

## Running Tests

### Run All Tests
```bash
cd backend
./mvnw test
```

### Run Only Unit Tests (No Spring Context)
```bash
./mvnw test -Dtest="ActivateProjectServiceTest,CompleteTaskServiceTest"
```

## Required Tests (5 Minimum)

All 5 required tests are implemented and passing:

| # | Test Name | Class | Status |
|---|-----------|-------|--------|
| 1 | `ActivateProject_WithTasks_ShouldSucceed` | ActivateProjectServiceTest | ✅ |
| 2 | `ActivateProject_WithoutTasks_ShouldFail` | ActivateProjectServiceTest | ✅ |
| 3 | `ActivateProject_ByNonOwner_ShouldFail` | ActivateProjectServiceTest | ✅ |
| 4 | `CompleteTask_AlreadyCompleted_ShouldFail` | CompleteTaskServiceTest | ✅ |
| 5 | `CompleteTask_ShouldGenerateAuditAndNotification` | CompleteTaskServiceTest | ✅ |

## Test Details

### ActivateProjectServiceTest (6 tests)

Tests for `ActivateProjectUseCase`:

1. **ActivateProject_WithTasks_ShouldSucceed**
   - Verifies that a DRAFT project with tasks can be activated
   - Checks status changes to ACTIVE
   - Verifies audit log and notification are generated

2. **ActivateProject_WithoutTasks_ShouldFail**
   - Verifies that a project without tasks cannot be activated
   - Throws `ProjectCannotBeActivatedException`
   - Verifies no save/audit/notification calls

3. **ActivateProject_ByNonOwner_ShouldFail**
   - Verifies only the project owner can activate
   - Throws `UnauthorizedAccessException`

4. **ActivateProject_ProjectNotFound_ShouldFail**
   - Verifies behavior when project doesn't exist
   - Throws `ProjectNotFoundException`

5. **ActivateProject_AlreadyActive_ShouldFail**
   - Verifies cannot activate an already active project
   - Throws `ProjectCannotBeActivatedException`

6. **ActivateProject_DeletedProject_ShouldFail**
   - Verifies cannot activate a soft-deleted project
   - Throws `ProjectNotFoundException`

### CompleteTaskServiceTest (7 tests)

Tests for `CompleteTaskUseCase`:

1. **CompleteTask_ShouldSucceed**
   - Verifies that an incomplete task can be completed
   - Checks completed status changes to true

2. **CompleteTask_ShouldGenerateAuditAndNotification**
   - Verifies audit log is registered with action "COMPLETE_TASK"
   - Verifies notification is sent with "completed" message

3. **CompleteTask_AlreadyCompleted_ShouldFail**
   - Verifies cannot complete an already completed task
   - Throws `TaskCannotBeCompletedException`
   - Verifies no save/audit/notification calls

4. **CompleteTask_TaskNotFound_ShouldFail**
   - Verifies behavior when task doesn't exist
   - Throws `TaskNotFoundException`

5. **CompleteTask_ByNonOwner_ShouldFail**
   - Verifies only the project owner can complete tasks
   - Throws `UnauthorizedAccessException`

6. **CompleteTask_DeletedTask_ShouldFail**
   - Verifies cannot complete a soft-deleted task
   - Throws `TaskNotFoundException`

7. **CompleteTask_DeletedProject_ShouldFail**
   - Verifies cannot complete task if project is deleted
   - Throws `ProjectNotFoundException`

## Test Characteristics

- ✅ **No Spring Context**: Tests use `@ExtendWith(MockitoExtension.class)` - fast execution
- ✅ **All Ports Mocked**: `@Mock` annotations for all output ports
- ✅ **Application Layer Focus**: Tests only in `application.service` package
- ✅ **JUnit 5**: Using `@Test`, `@DisplayName`, `@BeforeEach`
- ✅ **Mockito**: Using `when()`, `verify()`, `never()`, `any()`, `eq()`, `contains()`

## Test Output Example

```
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 - ActivateProjectServiceTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0 - CompleteTaskServiceTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
