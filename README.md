# Extended Problem Detail

A Spring Boot Starter for comprehensive exception handling with extended ProblemDetail responses (RFC 9457).

## Features

- **Spring Boot Starter**: Auto-configuration for easy integration
- **Global exception handler**: Extends `ResponseEntityExceptionHandler`
- **Standardized error responses**: Using `ProblemDetail` (RFC 9457)
- **Extended error details**: Custom `ExtendedProblemDetail` with field-level error information
- **Configurable**: Enable/disable exception handling
- **Full test coverage**: 50+ test methods for 35+ exception classes

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.github.sbracely</groupId>
    <artifactId>extended-problem-detail-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Configure (Optional)

```yaml
extended:
  problem-detail:
    enabled: true              # Enable/disable exception handling
```

### 3. Done!

The starter auto-configures exception handling for your Spring MVC application.

## Exception Test Coverage

See [MVC Exception Test Mapping](extended-problem-detail-test-mvc/mvc-exception-test-mapping.md) for the complete test coverage table.

The project covers **50+ test methods** for **35+ exception classes** including:
- 20 base Spring MVC exceptions (HttpRequestMethodNotSupportedException, HttpMediaTypeNotSupportedException, etc.)
- 15 subclasses (MissingMatrixVariableException, MissingRequestCookieException, etc.)
- ResponseStatusException hierarchy (MethodNotAllowedException, NotAcceptableStatusException, etc.)

## Project Structure

```
.
├── extended-problem-detail-autoconfigure/     # Auto-configuration module
│   └── src/main/java/com/github/sbracely/extended/problem/detail/
│       ├── extendedProblemDetailAutoConfiguration.java
│       ├── extendedProblemDetailProperties.java
│       └── handler/RequestExceptionHandler.java
├── extended-problem-detail-spring-boot-starter/  # Starter module
├── extended-problem-detail-test-flux/          # MVC test module
│   └── src/test/java/com/github/sbracely/extended/problem/detail/test/flux/
├── extended-problem-detail-test-flux/         # WebFlux test module (TODO)
└── pom.xml
```

## Building from Source

```bash
./mvnw clean install
```

## Running Tests

```bash
# Run all tests
./mvnw test

# Run MVC tests only
./mvnw test -pl extended-problem-detail-test-flux
```

## Requirements

- Java 17+
- Spring Boot 4.x

## License

Apache License 2.0
