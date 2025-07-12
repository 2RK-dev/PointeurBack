# Pointeur Back

Pointeur Back is a backend service built with Java and Spring Boot. It serves as the backend component for the Pointeur
application, providing RESTful APIs and database management.

## Features

- RESTful API endpoints
- Database migration support (Flyway)
- Docker Compose support for easy deployment
- Gradle build system

## Project Structure

```
├── build.gradle                # Gradle build configuration
├── docker-compose.yml          # Docker Compose setup
├── src/
│   ├── main/
│   │   ├── java/io/github/two_rk_dev/pointeurback/  # Java source code
│   │   └── resources/          # Application resources (YAML configs, migrations, etc.)
│   └── test/                   # Test sources
├── build/                      # Build output
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 7+
- Docker (for containerized deployment)

### Installation and Configuration

1. Copy the example environment file to create your own configuration:
   ```bash
   cp .env.example .env
   ```
   Edit the `.env` file as needed to configure your environment variables.

### Build and Run

#### 1. Build the project

```bash
./gradlew build
```

#### 2. Run Docker Compose

```bash
docker-compose up -d
```

#### 3. Run the application

```bash
./gradlew bootRun
```

### Database Migrations

Database migrations are handled using Flyway. Migration scripts are located in `src/main/resources/db/migration/`.

## Testing

Run tests with:

```bash
./gradlew test
```

## License

This project is licensed under the MIT License.
