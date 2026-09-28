# Checkpoint

| Step | Status |
|---|---|
| 1 — understand/design | Complete |
| 2 — recruitment implementation | Complete |
| 5–10 — build, tests, boot, change log | Complete |
| 11–13 — commit, reports, deploy verification | Complete |

## Decisions
- Brownfield Java/Gradle project; retain Spring Boot 2.7.18, Java 17 release compilation, PostgreSQL/JPA production configuration, JWT authentication, and `/api/v1` HR conventions.
- Recruitment routes remain `/api/recruitment/**` and use `ROLE_ADMIN` protection.
- Gradle wrapper is 8.5 for JDK 21 host compatibility; test JVM memory is constrained for this environment.
- Missing recruitment references use `RecruitmentNotFoundException`, mapped by `RecruitmentController` to the existing 404 response behavior.
- `JobOpeningSummaryResponse.stageCounts` is the required public response field.
- The local-only H2 profile and profile-scoped administrator exist solely for boot verification; production remains PostgreSQL.

## Completed verification
- Exact `./gradlew test` and `./gradlew build` commands succeeded; committed `gradle.properties` supplies the constrained JVM settings.
- Application booted successfully with `SPRING_PROFILES_ACTIVE=local`; real protected recruitment requests passed, including an observed 404 for a missing opening.
- Reports were regenerated from observed results in `tests-artifacts/test_results.json`, `api_test_report.xlsx`, and `changes_report.docx`.
