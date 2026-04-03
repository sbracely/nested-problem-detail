# Extended Problem Detail

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://openjdk.org/)

A Spring Boot starter library that provides enhanced ProblemDetail exception handling with field-level error information, fully compliant with [RFC 9457](https://datatracker.ietf.org/doc/html/rfc9457) (Problem Details for HTTP APIs).

## Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Usage Examples](#usage-examples)
- [Supported Exception Types](#supported-exception-types)
- [Core Classes](#core-classes)
- [Testing](#testing)
- [Version Compatibility](#version-compatibility)
- [Development Guide](#development-guide)
- [Contributing](#contributing)
- [License](#license)
- [Authors](#authors)
- [Related Links](#related-links)

## Project Overview

Extended Problem Detail is a Spring Boot starter library designed to enhance API error responses by extending Spring Framework's `ProblemDetail` with detailed field-level validation error information. When validation fails, instead of receiving a generic error message, API consumers get precise information about which fields failed validation and why.

### Problem Solved

Traditional Spring Boot validation error responses often lack detailed field-level information, making it difficult for API consumers to understand and fix validation issues. This library solves this by providing:

- Field-level error details (field name, error type, error message)
- Support for various parameter sources (query parameters, headers, cookies, path variables, request body)
- Consistent error response format following RFC 9457
- Automatic integration with Spring's validation framework

## Key Features

- **RFC 9457 Compliant**: Follows the Problem Details for HTTP APIs standard
- **Field-Level Error Details**: Provides specific field names and error messages for validation failures
- **Multiple Parameter Source Support**: Handles errors from @RequestParam, @RequestHeader, @CookieValue, @PathVariable, @RequestBody, and more
- **WebMVC & WebFlux Support**: Available for both synchronous and reactive Spring applications
- **Zero Configuration**: Auto-configuration with sensible defaults
- **Customizable**: Enable/disable via configuration properties
- **Visitor Pattern Implementation**: Efficiently processes different parameter validation results

## Project Structure

```
extended-problem-detail/
├── core/                              # Core module
│   └── src/main/java/.../core/
│       ├── response/                  # Response model classes
│       │   ├── ExtendedProblemDetail.java  # Extended ProblemDetail with error list
│       │   └── Error.java            # Individual error details (record)
│       └── logging/                   # Logging utilities
│           └── ExtendedProblemDetailLog.java  # Configurable logger for validation exceptions
├── autoconfigure-webmvc/              # WebMVC auto-configuration
│   └── src/main/java/.../mvc/
│       ├── MvcExtendedProblemDetailAutoConfiguration.java
│       ├── MvcExtendedProblemDetailProperties.java
│       └── handler/
│           └── MvcExtendedProblemDetailExceptionHandler.java
├── autoconfigure-webflux/             # WebFlux auto-configuration
│   └── src/main/java/.../flux/
│       ├── FluxExtendedProblemDetailAutoConfiguration.java
│       ├── FluxExtendedProblemDetailProperties.java
│       └── handler/
│           └── FluxExtendedProblemDetailExceptionHandler.java
├── starter-webmvc/                    # WebMVC starter (convenience dependency)
└── starter-webflux/                   # WebFlux starter (convenience dependency)
```

### Module Dependencies

```
starter-webmvc
    └── autoconfigure-webmvc
            └── core

starter-webflux
    └── autoconfigure-webflux
            └── core
```

## Quick Start

### 1. Add Dependency

For Spring WebMVC applications:

```xml
<dependency>
    <groupId>com.github.sbracely</groupId>
    <artifactId>extended-problem-detail-spring-boot-starter-webmvc</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

For Spring WebFlux applications:

```xml
<dependency>
    <groupId>com.github.sbracely</groupId>
    <artifactId>extended-problem-detail-spring-boot-starter-webflux</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Configure (Optional)

By default, the feature is enabled with DEBUG log level. You can customize configuration in `application.yml`:

```yaml
extended:
  problem-detail:
    enabled: true
    log-level: DEBUG  # TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
```

Or in `application.properties`:

```properties
extended.problem-detail.enabled=true
extended.problem-detail.log-level=DEBUG
```

### 3. That's It

The library automatically configures itself and starts enhancing validation error responses.

## Usage Examples

### Basic Validation Error Response

When a validation error occurs, the response will include detailed field information:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "email",
      "message": "must be a well-formed email address"
    },
    {
      "type": "PARAMETER",
      "target": "age",
      "message": "must be greater than or equal to 18"
    }
  ]
}
```

### Controller with Validation

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody UserRequest request) {
        // Implementation
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @PathVariable @Positive Long id) {
        // Implementation
    }

    @GetMapping
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam @NotBlank String name,
            @RequestHeader @NotNull String apiKey) {
        // Implementation
    }
}
```

### Request DTO with Validation

```java
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Email(message = "Must be a valid email address")
    private String email;

    @Min(value = 18, message = "Must be at least 18 years old")
    private Integer age;

    // Getters and setters
}
```

### Custom Error Response

This library supports `ErrorResponseException` (Spring 6's standard exception for RFC 9457) as the base class for custom business exceptions. You have two options:

#### Option 1: Use ErrorResponseException Directly

For simple cases, throw `ErrorResponseException` directly with an `ExtendedProblemDetail`:

```java
@Service
public class UserService {

    public User createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            ExtendedProblemDetail problem = new ExtendedProblemDetail();
            problem.setStatus(HttpStatus.CONFLICT.value());
            problem.setTitle("User already exists");
            problem.setDetail("A user with this email already exists");

            List<Error> errors = new ArrayList<>();
            errors.add(new Error(Error.Type.BUSINESS, "email", "Email is already registered"));
            problem.setErrors(errors);

            throw new ErrorResponseException(HttpStatus.CONFLICT, problem, null);
        }
        // Implementation
    }
}
```

#### Option 2: Extend ErrorResponseException

For reusable business exceptions, create a custom exception class:

```java
public class InsufficientBalanceException extends ErrorResponseException {

    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requiredAmount) {
        super(HttpStatus.PAYMENT_REQUIRED, createProblemDetail(currentBalance, requiredAmount), null);
    }

    private static ExtendedProblemDetail createProblemDetail(BigDecimal current, BigDecimal required) {
        ExtendedProblemDetail detail = new ExtendedProblemDetail();
        detail.setStatus(HttpStatus.PAYMENT_REQUIRED.value());
        detail.setTitle("Insufficient Balance");
        detail.setDetail(String.format("Current balance %s is less than required amount %s", current, required));

        List<Error> errors = new ArrayList<>();
        errors.add(new Error(Error.Type.BUSINESS, "balance", "Insufficient balance"));
        errors.add(new Error(Error.Type.BUSINESS, "amount", "Payment amount exceeds balance"));
        detail.setErrors(errors);

        return detail;
    }
}
```

Then use it in your service:

```java
@Service
public class PaymentService {

    public void processPayment(BigDecimal amount) {
        BigDecimal balance = getCurrentBalance();
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(balance, amount);
        }
        // Process payment
    }
}
```

Both approaches will be handled automatically by the library's exception handlers, and the `errors` field in `ExtendedProblemDetail` will be preserved in the response.

## Supported Exception Types

The library handles the following exception types:

| Exception Type | Description | WebMVC | WebFlux |
|---------------|-------------|--------|---------|
| `MethodArgumentNotValidException` | @Valid annotation validation failure | Yes | No |
| `HandlerMethodValidationException` | Method parameter validation failure | Yes | Yes |
| `WebExchangeBindException` | Data binding exception | Yes | Yes |
| `MethodValidationException` | Method-level validation exception | Yes | Yes |

### Parameter Annotations Supported

The following Spring annotations are supported for validation:

- `@RequestParam` - Query parameters (type: PARAMETER)
- `@RequestHeader` - HTTP headers (type: HEADER)
- `@CookieValue` - Cookies (type: COOKIE)
- `@PathVariable` - Path variables (type: PARAMETER)
- `@RequestBody` - Request body with @Valid (type: PARAMETER)
- `@ModelAttribute` - Model attributes (type: PARAMETER)
- `@RequestPart` - Multipart parts (type: PARAMETER)
- `@MatrixVariable` - Matrix variables (type: PARAMETER)

## Core Classes

### ExtendedProblemDetail

Extends Spring's `ProblemDetail` to add a list of `Error` objects:

```java
public class ExtendedProblemDetail extends ProblemDetail {
    private List<Error> errors;
    // Getters and setters
}
```

### Error

Represents individual error information as a Java record:

```java
public record Error(Type type, String target, String message) {
    public enum Type { PARAMETER, COOKIE, HEADER, BUSINESS }
}
```

### Exception Handlers

- **MvcExtendedProblemDetailExceptionHandler**: Handles exceptions for Spring WebMVC applications
- **FluxExtendedProblemDetailExceptionHandler**: Handles exceptions for Spring WebFlux applications

Both handlers extend Spring's `ResponseEntityExceptionHandler` and use the Visitor pattern to process different types of parameter validation results.

## Testing

The library includes comprehensive test suites for both WebMVC and WebFlux modules.

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests for specific module
./mvnw test -pl autoconfigure-webmvc
./mvnw test -pl autoconfigure-webflux
```

### Test Structure

```
autoconfigure-webmvc/src/test/java/.../test/mvc/
├── controller/
│   └── MvcProblemDetailController.java
├── request/
│   ├── ProblemDetailRequest.java
│   └── valid/
│       ├── annotation/
│       └── validator/
├── service/
│   └── ProblemDetailService.java
└── test/
    ├── MvcExtendedProblemDetailTests.java
    └── MvcExtendedProblemDetailRandomPortTests.java
```

## Version Compatibility

| Component | Version |
|-----------|---------|
| Spring Boot | 4.0.5 |
| Java | 17+ |
| Spring Framework | 6.2.x |

## Development Guide

### Building the Project

```bash
# Build all modules
./mvnw clean install

# Skip tests
./mvnw clean install -DskipTests
```

### Module Development

When adding new features:

1. **Core Module**: Add shared classes to `core` module
   - Response models go in `core/response/`
   - Logging utilities go in `core/logging/`
2. **WebMVC Support**: Implement handlers in `autoconfigure-webmvc`
3. **WebFlux Support**: Implement handlers in `autoconfigure-webflux`
4. **Tests**: Add corresponding tests in each module's test directory

### Configuration Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `extended.problem-detail.enabled` | Boolean | true | Enable/disable extended problem detail handling |
| `extended.problem-detail.log-level` | LogLevel | DEBUG | Log level for validation exceptions (TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF) |

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow standard Java conventions
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Write unit tests for new features

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) file for details.

```
Copyright 2025 sbracely

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Authors

- **sbracely** - Initial work - [GitHub](https://github.com/sbracely)

## Related Links

- [RFC 9457 - Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc9457)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Framework - ProblemDetail](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ProblemDetail.html)
- [Bean Validation Specification](https://beanvalidation.org/)
- [Project Repository](https://github.com/sbracely/extended-problem-detail)
