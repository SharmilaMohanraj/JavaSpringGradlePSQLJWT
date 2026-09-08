# JavaSpringMicroGradlePSQLJWT2

HR backend for employees, departments, designations, leave workflow, payroll, performance reviews and reporting hierarchy.

## Run
`chmod +x start.sh && bash start.sh` starts on port 24480. Docker is intentionally not supplied.

## Environment
Load `.env_50b38c46-0195-4b5c-9b2b-c44cb07ca7bd` with DB_URL, DB_USER, DB_PASSWORD and JWT_SECRET.

## API
`POST /api/v1/auth/register`, `POST /api/v1/auth/login`; CRUD paths are `/api/v1/departments`, `/designations`, `/employees`, `/leave-requests`, `/payroll-records`, `/performance-reviews`, `/hierarchies`. Manager/admin may update leave status; admin may request `/api/v1/reports/leave-balances`. Swagger: `/docs`.

## Tests
Run `./gradlew compileJava -q`, then exercise APIs at `http://localhost:24480`.
