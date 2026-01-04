# Copilot Instructions for EntryPoint

## Repository Overview

**EntryPoint** is an event booking and management application built with a Spring Boot backend and React frontend (frontend not yet in repository). The backend provides REST APIs for managing events, performers, tickets, and user authentication.

**Repository Size**: ~63MB (backend only, with Gradle wrapper and dependencies)
**Main Language**: Java 17
**Build Tool**: Gradle 9.2.1
**Framework**: Spring Boot 4.0.1
**Database**: MySQL (production), H2 (tests)

## Project Structure

```
entrypoint/
├── LICENSE                    # AGPL-3.0 license
├── README.md                  # Project documentation
└── entrypoint-backend/        # Spring Boot backend application
    ├── build.gradle.kts       # Gradle build configuration
    ├── settings.gradle.kts    # Gradle settings
    ├── gradlew / gradlew.bat  # Gradle wrapper scripts
    ├── .gitignore             # Git ignore rules
    ├── .gitattributes         # Git attributes for line endings
    └── src/
        ├── main/
        │   ├── java/com/lamergameryt/entrypoint/
        │   │   ├── EntrypointApplication.java    # Main Spring Boot application
        │   │   ├── config/                       # Configuration classes
        │   │   │   ├── CorsConfig.java           # CORS configuration
        │   │   │   └── OpenAPIConfig.java        # OpenAPI/Swagger configuration
        │   │   ├── controller/                   # REST controllers
        │   │   │   ├── EventController.java      # Event & ticket endpoints
        │   │   │   └── UserController.java       # User & auth endpoints
        │   │   ├── dto/                          # Data Transfer Objects
        │   │   │   ├── EventDto.java
        │   │   │   ├── PerformerDto.java
        │   │   │   ├── TicketDto.java
        │   │   │   ├── UserDto.java
        │   │   │   └── request/                  # Request DTOs
        │   │   ├── enums/                        # Enumerations
        │   │   │   └── TicketStatus.java
        │   │   ├── exception/                    # Custom exceptions
        │   │   │   └── ResourceNotFoundException.java
        │   │   ├── model/                        # JPA entity models
        │   │   │   ├── EventModel.java
        │   │   │   ├── PerformerModel.java
        │   │   │   ├── TicketModel.java
        │   │   │   └── UserModel.java
        │   │   ├── repository/                   # Spring Data repositories
        │   │   │   ├── EventRepository.java
        │   │   │   ├── TicketRepository.java
        │   │   │   └── UserRepository.java
        │   │   └── service/                      # Business logic services
        │   │       ├── EventService.java
        │   │       ├── TicketService.java
        │   │       └── UserService.java
        │   └── resources/
        │       └── application.properties        # Application configuration
        └── test/
            └── java/com/lamergameryt/entrypoint/
                ├── EntrypointApplicationTests.java    # Integration test
                └── db/
                    ├── DbTestBase.java               # Base class for DB tests
                    └── user/UserRepositoryTest.java  # User repository tests
```

## Architecture

**Pattern**: Standard Spring MVC with layered architecture
- **Controllers**: Handle HTTP requests/responses, validation
- **Services**: Business logic layer
- **Repositories**: Data access layer (Spring Data JPA)
- **Models**: JPA entities with Hibernate annotations
- **DTOs**: Data transfer objects for API contracts

**Key Technologies**:
- Spring Boot 4.0.1 (Web MVC, Data JPA, Validation)
- Hibernate 7.2.0 (ORM)
- Lombok (boilerplate reduction)
- SpringDoc OpenAPI 3.0.0 (API documentation at `/scalar/index.html`)
- MySQL Connector (production)
- H2 Database (tests)
- Spotless (code formatting)
- JUnit 5 + Spring Boot Test (testing)

## Build and Development Commands

### Prerequisites

**CRITICAL**: Before running **ANY** Gradle command, you **MUST** create an `env.properties` file in the `entrypoint-backend` directory. The application references this file in `application.properties` and will fail without it.

```bash
# Create env.properties with these required variables:
cd entrypoint-backend
cat > env.properties << 'EOF'
DB_DATABASE=entrypoint
DB_USERNAME=root
DB_PASSWORD=password
EOF
```

**Note**: `env.properties` is in `.gitignore` and should NEVER be committed.

### Runtime Requirements

- **Java**: JDK 17 (OpenJDK Temurin 17.0.17+10)
- **Gradle**: 9.2.1 (will auto-download via wrapper)
- **MySQL**: 8.0+ (for running the application)
- **No MySQL needed for**: building, testing (uses H2 in-memory DB)

### Build Commands

**ALWAYS work from the `entrypoint-backend` directory for all Gradle commands.**

```bash
cd entrypoint-backend
```

#### Clean Build Artifacts
```bash
./gradlew clean
# Takes: <1 second
# Success: "BUILD SUCCESSFUL"
```

#### Compile Code (No Tests)
```bash
./gradlew assemble
# Takes: ~3-5 seconds (after first run)
# Outputs: build/libs/entrypoint-backend-0.0.1.jar (61MB executable JAR)
# Success: "BUILD SUCCESSFUL"
```

#### Build with Tests
```bash
# IMPORTANT: Tests require env.properties but do NOT require MySQL
# DataJpaTest tests use H2 in-memory database
# SpringBootTest fails without MySQL - see workaround below

./gradlew build
# Takes: ~10 seconds
# WARNING: Will fail on EntrypointApplicationTests without MySQL running
# 3 DataJpaTest tests will pass, 1 SpringBootTest will fail
```

**Test Workaround**: To build successfully without MySQL, skip tests:
```bash
./gradlew build -x test
# Takes: ~3-5 seconds
# Success: "BUILD SUCCESSFUL"
# This is the RECOMMENDED build command for CI/CD without MySQL
```

#### Run Tests Only
```bash
./gradlew test
# Takes: ~8 seconds
# Requires: env.properties (but NOT MySQL for DataJpaTest tests)
# Note: EntrypointApplicationTests will fail without MySQL
```

### Code Quality Commands

#### Check Code Formatting
```bash
./gradlew spotlessCheck
# Takes: ~1-2 seconds
# Checks: Java files and misc files (.gradle, .gitattributes, .gitignore)
# Success: "BUILD SUCCESSFUL" if all files are formatted correctly
# Failure: Lists files that need formatting
```

#### Auto-Format Code
```bash
./gradlew spotlessApply
# Takes: ~2-3 seconds
# Applies: Palantir Java Format, removes unused imports, sorts imports
# Also formats: .gradle, .gitattributes, .gitignore files
# ALWAYS run this before committing code changes
```

#### Run All Checks
```bash
./gradlew check
# Takes: ~8-10 seconds
# Runs: spotlessCheck + test
# This is the comprehensive validation command
```

### Running the Application

```bash
./gradlew bootRun
# Requires: 
#   1. env.properties file (as described above)
#   2. MySQL running on localhost:3306
#   3. Database specified in DB_DATABASE must exist
# Application starts on: http://localhost:8080
# API Documentation: http://localhost:8080/scalar/index.html
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

**MySQL Setup Required**:
```bash
# Create database before running:
mysql -u root -p
CREATE DATABASE entrypoint;
```

### Common Build Issues and Solutions

#### Issue 1: "Config data resource 'file [env.properties]' cannot be found"
**Cause**: Missing `env.properties` file  
**Solution**: Create the file as shown in Prerequisites section above

#### Issue 2: "Unable to determine Dialect without JDBC metadata"
**Cause**: MySQL not running or connection failed  
**Solution**: 
- For building/testing: Use `./gradlew build -x test`
- For running: Start MySQL and create database
- For tests only: DataJpaTest tests work without MySQL (use H2)

#### Issue 3: Tests fail with "ApplicationContext failure"
**Cause**: `EntrypointApplicationTests` requires MySQL connection  
**Solution**: 
- Skip tests with `-x test` flag
- Or start MySQL before running tests
- Note: Repository tests in `db/` package work without MySQL

#### Issue 4: First Gradle command is slow (~40-60 seconds)
**Cause**: Gradle daemon initialization and dependency downloads  
**Solution**: This is normal on first run. Subsequent builds are faster (~3-5 seconds)

#### Issue 5: Spotless formatting failures
**Cause**: Code doesn't match formatting rules  
**Solution**: Run `./gradlew spotlessApply` to auto-fix

## Code Style and Conventions

### Formatting (Enforced by Spotless)

**ALWAYS run `./gradlew spotlessApply` before committing.**

The project uses:
- **Palantir Java Format** with Javadoc formatting
- **Cleanthat** with SafeButNotConsensual mutators
- Automatic import organization and unused import removal
- Trailing whitespace trimming
- Files end with newline
- Tabs in .gradle files, spaces in Java

### Code Conventions

1. **File Headers**: All Java files MUST include the AGPL-3.0 copyright header (see existing files)
2. **Lombok**: Use Lombok annotations (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, etc.)
3. **Validation**: Use Jakarta validation annotations in DTOs (`@NotNull`, `@Valid`, etc.)
4. **Documentation**: Use Javadoc for public APIs, especially controller methods
5. **Naming**:
   - Models: `*Model.java` (e.g., `EventModel`)
   - DTOs: `*Dto.java` (e.g., `EventDto`)
   - Request DTOs: `*RequestDto.java` (e.g., `EventCreateRequestDto`)
   - Repositories: `*Repository.java`
   - Services: `*Service.java`
   - Controllers: `*Controller.java`

### API Documentation

- Use **Javadoc comments** for controller methods (preferred over SpringDoc annotations)
- The project uses `therapi-runtime-javadoc` to convert Javadoc to OpenAPI
- Tag controllers with `@Tag` from `io.swagger.v3.oas.annotations.tags.Tag`
- Example: See `EventController.java` for proper Javadoc usage

## Testing Conventions

1. **Unit Tests**: Place in `src/test/java` mirroring package structure
2. **Repository Tests**: 
   - Extend `DbTestBase` (annotated with `@DataJpaTest`)
   - Use H2 in-memory database (no MySQL needed)
   - Example: `UserRepositoryTest.java`
3. **Integration Tests**: 
   - Use `@SpringBootTest`
   - Require MySQL connection
   - Example: `EntrypointApplicationTests.java`

**Test Running Strategy**:
- For development: `./gradlew test -x :test --tests "*Repository*"` (DB tests only)
- For CI without MySQL: `./gradlew build -x test`
- For full validation with MySQL: `./gradlew build`

## Dependencies and Configuration

### Key Dependencies (from build.gradle.kts)

- Spring Boot Starter Web MVC
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- SpringDoc OpenAPI (Swagger + Scalar UI)
- MySQL Connector J (runtime)
- H2 Database (test)
- Lombok (compile-time)
- JUnit 5 Platform

### Configuration Files

- `build.gradle.kts`: Main build configuration, dependencies, plugins
- `settings.gradle.kts`: Project name (`entrypoint-backend`)
- `application.properties`: Spring configuration, database connection
- `env.properties`: Environment variables (NOT in Git, create manually)
- `.gitignore`: Excludes build artifacts, IDE files, `env.properties`

## Validation Checklist for Code Changes

Before committing changes, ALWAYS:

1. ✅ Create/verify `env.properties` exists
2. ✅ Run `./gradlew spotlessApply` to format code
3. ✅ Run `./gradlew spotlessCheck` to verify formatting
4. ✅ Run `./gradlew build -x test` to verify compilation
5. ✅ Run relevant tests if possible
6. ✅ Ensure AGPL-3.0 headers are on new files
7. ✅ Update Javadoc for public API changes
8. ✅ Do NOT commit `env.properties`, build artifacts, or IDE files

## Quick Reference

```bash
# Standard development workflow
cd entrypoint-backend

# First time setup
cat > env.properties << 'EOF'
DB_DATABASE=entrypoint_test
DB_USERNAME=test  
DB_PASSWORD=test
EOF

# Make code changes, then:
./gradlew spotlessApply              # Format code
./gradlew build -x test              # Build without MySQL
# OR with MySQL running:
./gradlew build                      # Build with tests

# Run application (requires MySQL)
./gradlew bootRun
```

## Important Notes

- **Trust these instructions**: Only search for additional information if these instructions are incomplete or incorrect
- **Database is optional** for building and most testing, but required for running the application
- **env.properties is required** for ALL Gradle tasks, even though it's not committed to Git
- **First Gradle run is slow** (~40-60 seconds) due to initialization and downloads
- **Frontend is not yet in this repository** - only backend code exists currently
- **No CI/CD workflows** are configured yet - this is a local development setup only
