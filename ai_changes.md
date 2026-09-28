# AI Changes — Recruitment Extension

## Added and corrected
- Recruitment domain model, grouped candidate-stage counts, transactional service operations, and administrator-protected `/api/recruitment/**` routes.
- `JobOpeningSummaryResponse.stageCounts` (`Map<CandidateStage, Long>`) is now the public JSON contract; the former `candidateStageCounts` response property is not exposed.
- `RecruitmentNotFoundException` plus a controller exception handler now return HTTP 404 for missing department, designation, job opening, or candidate references rather than an unhandled 500.
- Focused Mockito service tests and MockMvc controller tests cover stage counts, the JSON property contract, and 404 mapping.
- A `local` H2 verification profile and profile-scoped administrator permit repeatable application boot and protected-route verification without modifying production PostgreSQL configuration.

## Tooling and startup corrections
- Updated the committed Gradle wrapper to 8.5 so it runs on the available JDK 21 while the project continues compiling with Java 17 release settings.
- Constrained Gradle test execution memory for the verification environment.
- Corrected `start.sh` to execute the Spring Boot jar rather than the plain jar.

## Observed verification
- `./gradlew test` completed successfully.
- `./gradlew build` completed successfully.
- The app booted successfully on port 24480 using the local profile. Real authenticated HTTP checks passed for department/designation creation, recruitment opening and candidate creation, stage changes including hiring, opening listing with `stageCounts`, and a missing-opening 404 response.
- See `tests-artifacts/test_results.json`, `tests-artifacts/api_test_report.xlsx`, and `tests-artifacts/changes_report.docx` for observed results.
