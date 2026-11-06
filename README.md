# IncidentIQ

IncidentIQ is a Java 21 Spring Boot modular monolith for learning backend development and interview preparation.

## Requirements

- Java 21
- Maven 3.6.3 or newer
- PostgreSQL for database-backed functionality and a healthy Actuator database check

## Run

Make sure `JAVA_HOME` points to a Java 21 installation. On macOS, you can select one with:

```sh
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
```

Set the datasource variables before starting the app. The URL defaults to the local database shown below; username and password are required:

```sh
export DB_URL=jdbc:postgresql://localhost:5432/incidentiq
export DB_USERNAME=incidentiq
export DB_PASSWORD=your-local-database-password
mvn spring-boot:run
```

Alternatively, put these values in a local `.env` file and load it before running the app:

```sh
set -a
source .env
set +a
mvn spring-boot:run
```

Create the local role and database once in PostgreSQL, using a password you choose:

```sql
CREATE ROLE incidentiq LOGIN PASSWORD 'choose-a-local-password';
CREATE DATABASE incidentiq OWNER incidentiq;
```

Keep the password in your shell environment or a local `.env` file. `.env` is ignored by Git. The application requires `DB_USERNAME` and `DB_PASSWORD`; it does not contain a fallback password.

## Verify

```sh
mvn test
```

The PostgreSQL integration test is opt-in and uses only the dedicated `incidentiq_test` database:

```sh
set -a
source .env
set +a
RUN_POSTGRES_INTEGRATION=true mvn test
```

- `GET http://localhost:8080/api/health` — basic application response.
- `GET http://localhost:8080/actuator/health` — Actuator health, including the PostgreSQL connection.
- `GET http://localhost:8080/actuator/info` — application information.

## Project layout

- `com.incidentiq`: application bootstrap.
- `com.incidentiq.health`: HTTP health endpoint.
- `src/main/resources`: externalized application configuration.
- `src/test`: application context smoke test.

Business modules will be added here as cohesive packages inside this single application.
