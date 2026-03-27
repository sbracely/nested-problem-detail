# Nested Problem Detail

A Spring Boot project demonstrating comprehensive exception handling with nested ProblemDetail responses.

## Features

- Global exception handler extending `ResponseEntityExceptionHandler`
- Standardized error responses using `ProblemDetail` (RFC 9457)
- Nested error details with custom `NestedProblemDetail`
- Full test coverage for Spring MVC exceptions

## Exception Test Coverage

See [MVC Exception Test Mapping](doc/mvc-exception-test-mapping.md) for the complete test coverage table.

The project covers **50 test methods** for **35 exception classes** including:
- 20 base Spring MVC exceptions (HttpRequestMethodNotSupportedException, HttpMediaTypeNotSupportedException, etc.)
- 15 subclasses (MissingMatrixVariableException, MissingRequestCookieException, etc.)
- ResponseStatusException hierarchy (MethodNotAllowedException, NotAcceptableStatusException, etc.)

## Project Structure

```
.
├── src/
│   ├── main/java/org/example/exceptionhandlerexample/
│   │   ├── handler/          # Global exception handlers
│   │   ├── response/         # ProblemDetail and error responses
│   │   └── App.java
│   └── test/
│       └── java/org/example/exceptionhandlerexample/
│           └── test/controller/  # Exception test classes
├── doc/
│   └── mvc-exception-test-mapping.md  # Detailed test mapping
└── pom.xml
```

## Running Tests

```bash
./mvnw test
```

## License

MIT
