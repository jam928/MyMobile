# MyMobile

Spring Boot 4 (Spring MVC + Thymeleaf, Spring Data JPA / Hibernate 7) app backed by MySQL, with phone photos stored in MinIO.

## Run everything with Docker

```sh
docker compose up -d
```

The `app` service runs `mvn spring-boot:run` against the mounted source (dependencies are cached in the
`maven-repo` volume). After changing code, run `docker compose restart app`.

| Service      | URL                                   | Credentials             |
|--------------|---------------------------------------|-------------------------|
| App          | http://localhost:8080/MyMobile/       |                         |
| MySQL        | localhost:3306, database `mymobile`   | `mymobile` / `mymobile` |
| MinIO API    | http://localhost:9000                 | `minioadmin` / `minioadmin` |
| MinIO console| http://localhost:9001                 | `minioadmin` / `minioadmin` |

- Flyway applies the migrations in `src/main/resources/db/migration` when the app starts (schema, sample phones/plans, ...).
  To change the schema, add a new `V<next number>__description.sql` file; never edit a migration that has already run.
- The `minio-init` job creates the `phones` bucket and uploads `docker/minio/seed/*`.
- `phones.img_src` holds the MinIO object key; the app serves photos at `/MyMobile/photos/{key}`.

API docs (Swagger UI): http://localhost:8080/MyMobile/swagger-ui.html. The phone endpoints are public;
the admin endpoints use the website's sign-in, so sign in as an admin in the same browser first.

Reset all data with `docker compose down -v`.

## Run the app locally against the containers

```sh
docker compose up -d mysql minio minio-init
./mvnw spring-boot:run
```

Connection settings live in `src/main/resources/application.properties` and can be overridden with
environment variables (`SPRING_DATASOURCE_URL`, `MINIO_ENDPOINT`, `MINIO_BUCKET`, ...).

## Tests

- **Unit tests** (`src/test/java/.../*Test.java`): JUnit 5 + Mockito. Services, mappers, upload checks and
  controllers (with MockMvc and mocked services). Run with `./mvnw test`.
- **Integration tests** (`src/test/java/.../it/*IT.java`): the whole app against a real MySQL 8.4 started by
  Testcontainers, including every Flyway migration, Spring Security (login, CSRF, roles) and the JSON API.
  Each test runs in a transaction that is rolled back. Run with `./mvnw verify` (needs Docker).
- **Coverage**: JaCoCo, unit + integration tests together. HTML report in `target/site/jacoco/index.html`.

Run everything in Docker and print the results and a coverage summary at the end:

```sh
docker compose run --rm tests
```

The `tests` service builds a copy of the sources (so it doesn't interfere with the running `app`), starts
MySQL through the host's Docker socket, and copies the HTML coverage report to `target/coverage/index.html`.
Locally, `./mvnw verify && scripts/coverage-summary.sh` prints the same summary.
