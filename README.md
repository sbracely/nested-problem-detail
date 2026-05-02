# Extended Problem Detail Boot 4

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0%2B-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://openjdk.org/)

A Spring Boot 4 starter that extends [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) `ProblemDetail` responses with
field-level validation error details for both Spring WebMVC and Spring WebFlux.

## Installation

Choose the starter that matches your web stack and Spring Boot line.

| Spring Boot line | Branch | Artifact prefix | Version line |
|------|--------|-----------------|--------------|
| `3.x` | `3.x` | `extended-problem-detail-boot3-*` | Use the latest `3.x` release |
| `4.x` | `main` / `4.x` | `extended-problem-detail-boot4-*` | Use the latest `4.x` release |

### Spring Boot 4 WebMVC

```xml

<dependency>
    <groupId>io.github.sbracely</groupId>
    <artifactId>extended-problem-detail-boot4-webmvc-spring-boot-starter</artifactId>
    <version>4.0.1</version>
</dependency>
```

### Spring Boot 4 WebFlux

```xml

<dependency>
    <groupId>io.github.sbracely</groupId>
    <artifactId>extended-problem-detail-boot4-webflux-spring-boot-starter</artifactId>
    <version>4.0.1</version>
</dependency>
```

No additional configuration is required. The exception handler registers automatically.

## Compatibility

| Scope | Version | Notes |
|------|---------|-------|
| Spring Boot 4 line | `4.0.x` | Use `extended-problem-detail-boot4-*` artifacts |
| Minimum Java | `17+` | Project source and public API target Java 17 or newer |
| Verified in this repository | Spring Boot `4.0.6` / Java `25.0.2` | Current reactor test run covers the Boot 4 line |

## Example OpenAPI Documents

Both example applications expose OpenAPI documents at runtime:

- WebMVC example: `/v3/api-docs`, `/v3/api-docs.yaml`, and `/swagger-ui/index.html`
- WebFlux example: `/v3/api-docs`, `/v3/api-docs.yaml`, and `/swagger-ui/index.html`

The runtime OpenAPI documents focus on the `application/problem+json` exception payload and its
`errors` / `Error` structure. For concrete request parameters and ways to trigger each
error response, refer to the example controller tests in each module.

When the example applications are running, `/swagger-ui/index.html` provides interactive "Try it out"
requests against the live example endpoints.

### Offline OpenAPI Exports

Each example module also provides a Maven profile that generates offline OpenAPI spec files
without requiring you to start the application manually. The profile is enabled by default, so
running the example module test lifecycle also refreshes the exported files in `docs`.

Generate WebMVC offline docs:

```sh
./mvnw -pl webmvc/example -Poffline-openapi-docs verify
```

```powershell
.\mvnw.cmd -pl webmvc\example -Poffline-openapi-docs verify
```

Generate WebFlux offline docs:

```sh
./mvnw -pl webflux/example -Poffline-openapi-docs verify
```

```powershell
.\mvnw.cmd -pl webflux\example -Poffline-openapi-docs verify
```

Generated files are written to `docs` inside the example module:

- `openapi.json`
- `openapi.yaml`
- [WebMVC exception response table](webmvc/example/docs/webmvc-example-exception-response-table.md)
- [WebFlux exception response table](webflux/example/docs/webflux-example-exception-response-table.md)

These exported OpenAPI files make it easier to inspect which exceptions map to which
`application/problem+json` response bodies without manually starting the example service first.

## Response Format

When a validation exception occurs, the response extends the standard RFC 9457 body with an `errors` array:

```json
{
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/api/users",
  "errors": [
    {
      "type": "REQUEST_BODY",
      "target": "email",
      "message": "must be a well-formed email address"
    },
    {
      "type": "REQUEST_BODY",
      "target": "password",
      "message": "size must be between 8 and 20"
    }
  ]
}
```

On Spring Framework 7 / Spring Boot 4, `ProblemDetail.type` is not set by default, so the serialized
JSON usually omits the `type` field unless your application sets it explicitly.

### Error Object Fields

| Field     | Description                                                            |
|-----------|------------------------------------------------------------------------|
| `type`    | Error source: `QUERY_PARAMETER`, `PATH_VARIABLE`, `REQUEST_BODY`, `MODEL_ATTRIBUTE`, `REQUEST_PART`, `MATRIX_VARIABLE`, `COOKIE`, `HEADER`, or `BUSINESS` |
| `target`  | Field name, parameter name, or other identifier of the offending input |
| `message` | Human-readable description of the constraint violation                 |

### Handled Exceptions

| Exception                          | Trigger                                           |
|------------------------------------|---------------------------------------------------|
| `MethodArgumentNotValidException`  | `@Valid` on a `@RequestBody` or `@ModelAttribute` |
| `HandlerMethodValidationException` | `@Validated` on controller method parameters      |
| `WebExchangeBindException`         | Binding failure on a `@ModelAttribute`            |
| `MethodValidationException`        | Bean-level method validation via `@Validated`     |

## Configuration

```yaml
extended:
  problem-detail:
    enabled: true        # Set to false to disable the auto-configured handler (default: true)
    errors-property-name: errors  # ProblemDetail properties entry name for structured errors (default: errors)
    handler-order: 0     # @ControllerAdvice order for built-in handlers (default: 0)
    logging:
      at-level: INFO     # Level used to log caught exceptions: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF (default: INFO)
      print-stack-trace: false  # Include full stack trace in the log entry (default: false)
```

Equivalent `application.properties`:

```properties
extended.problem-detail.enabled=true
extended.problem-detail.errors-property-name=errors
extended.problem-detail.handler-order=0
extended.problem-detail.logging.at-level=INFO
extended.problem-detail.logging.print-stack-trace=false
```

If `extended.problem-detail.enabled` is omitted, the starter logs a one-time `INFO` message at startup
to make the default explicit and show how to disable it. Set the property explicitly to `true` or
`false` to suppress that reminder.

## Customization

### Throwing a Business Exception

For business-level errors, extend `ErrorResponseException` and populate the `ProblemDetail` body directly:

```java
public class OrderNotFoundException extends ErrorResponseException {

    public OrderNotFoundException(String orderId) {
        super(HttpStatus.NOT_FOUND,
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Order not found: " + orderId),
                null);
    }
}
```

To include structured `errors` in the response, add them to the `ProblemDetail` properties using the
configured entry name (`extended.problem-detail.errors-property-name`, default `errors`):

```java
public class OrderNotFoundException extends ErrorResponseException {

    public OrderNotFoundException(String orderId, String errorsPropertyName) {
        super(HttpStatus.NOT_FOUND, createBody(orderId, errorsPropertyName), null);
    }

    private static ProblemDetail createBody(String orderId, String errorsPropertyName) {
        ProblemDetail body =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Order not found: " + orderId);
        body.setProperty(errorsPropertyName, List.of(
                new Error(Error.Type.BUSINESS, "orderId", "Order not found: " + orderId)));
        return body;
    }
}
```

### Extending the Exception Handler

The auto-configured handler is registered as a `@ConditionalOnMissingBean`. Declaring your own subclass as a
Spring bean replaces it entirely.

Extend `MvcExtendedProblemDetailExceptionHandler` (WebMVC) or `FluxExtendedProblemDetailExceptionHandler` (WebFlux).
The base class exposes `logger` and `extendedProblemDetailLog` for use in overriding methods.

**Override a handler already covered by the base class:**

```java
@RestControllerAdvice
public class CustomExceptionHandler extends MvcExtendedProblemDetailExceptionHandler {

    public CustomExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
        super(extendedProblemDetailLog);
    }

    // Override to customise the response for 405 Method Not Allowed
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        extendedProblemDetailLog.log(logger, ex, "handleHttpRequestMethodNotSupported");
        return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }
}
```

**Add a handler for an exception type not covered by the base class:**

```java
@RestControllerAdvice
public class CustomExceptionHandler extends MvcExtendedProblemDetailExceptionHandler {

    public CustomExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
        super(extendedProblemDetailLog);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        extendedProblemDetailLog.log(logger, ex, "handleAccessDenied");
        ProblemDetail body = createProblemDetail(ex, HttpStatus.FORBIDDEN,
                "Access denied", null, null, request);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}
```

### Customizing `HandlerMethodValidationException` Resolution

`HandlerMethodValidationException` is resolved via the Visitor pattern. Each parameter annotation type has a
dedicated `resolveXxx` method in `ExtendedProblemDetailErrorResolver` that you can override to change how errors
are built.

Example — override `resolveCookieValue` to use the annotation's `name` attribute as the error target:

```java
@RestControllerAdvice
public class CustomExceptionHandler extends MvcExtendedProblemDetailExceptionHandler {

    public CustomExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
        super(extendedProblemDetailLog);
    }

    @Override
    protected void resolveCookieValue(HandlerMethodValidationException ex,
                                      CookieValue cookieValue,
                                      ParameterValidationResult result,
                                      List<Error> errorList) {
        String target = cookieValue.name().isEmpty()
                ? result.getMethodParameter().getParameterName()
                : cookieValue.name();
        addParameterErrors(result, Error.Type.COOKIE, target, errorList);
    }
}
```

All available override points:

| Method                               | Parameter annotation       |
|--------------------------------------|----------------------------|
| `resolveCookieValue`                 | `@CookieValue`             |
| `resolveMatrixVariable`              | `@MatrixVariable`          |
| `resolveModelAttribute`              | `@ModelAttribute`          |
| `resolvePathVariable`                | `@PathVariable`            |
| `resolveRequestBody`                 | `@RequestBody` (object)    |
| `resolveRequestBodyValidationResult` | `@RequestBody` (scalar)    |
| `resolveRequestHeader`               | `@RequestHeader`           |
| `resolveRequestParam`                | `@RequestParam`            |
| `resolveRequestPart`                 | `@RequestPart`             |
| `resolveOther`                       | other / unrecognized types |

## Modules

| Artifact                                                   | Description |
|------------------------------------------------------------|-------------|
| `extended-problem-detail-boot4-common`                     | Shared Boot 4 support layer |
| `extended-problem-detail-boot4-webmvc-autoconfigure`       | Boot 4 WebMVC auto-configuration |
| `extended-problem-detail-boot4-webmvc-spring-boot-starter` | Boot 4 WebMVC starter |
| `extended-problem-detail-boot4-webflux-autoconfigure`      | Boot 4 WebFlux auto-configuration |
| `extended-problem-detail-boot4-webflux-spring-boot-starter`| Boot 4 WebFlux starter |

The root, `webmvc`, and `webflux` aggregator POMs are reactor-only and are not published to Maven Central.

## Related Links

- [RFC 9457 - Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc9457)
- [Spring Framework - ProblemDetail](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ProblemDetail.html)
