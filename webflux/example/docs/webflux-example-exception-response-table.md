# WebFlux Example Application Exception Response Table

- Data source: response bodies come from the current `3.x` branch `openapi.json` examples that are kept aligned with runtime by contract tests; controller-only scenarios are added from the current controller tests.
- Ordering: follows the current `ResponseEntityExceptionHandler#handleException` dispatch order on the `3.x` line; subclasses are placed next to their parent class.

## 1. `org.springframework.web.server.MethodNotAllowedException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
DELETE /flux-extended-problem-detail/method-not-allowed-exception
None
```

**Response**

```http
status: 405
Content-Type: application/problem+json
Allow: GET

{
  "type": "about:blank",
  "title": "Method Not Allowed",
  "status": 405,
  "detail": "Supported methods: [GET]",
  "instance": "/flux-extended-problem-detail/method-not-allowed-exception"
}
```

## 2. `org.springframework.web.server.NotAcceptableStatusException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/not-acceptable-status-exception
Header: Accept=application/xml
```

**Response**

```http
status: 406
Content-Type: application/problem+json
Accept: application/json

{
  "type": "about:blank",
  "title": "Not Acceptable",
  "status": 406,
  "detail": "Acceptable representations: [application/json].",
  "instance": "/flux-extended-problem-detail/not-acceptable-status-exception"
}
```

## 3. `org.springframework.web.server.UnsupportedMediaTypeStatusException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /flux-extended-problem-detail/unsupported-media-type-status-exception
None (Content-Type: application/xml not sent)
```

**Response**

```http
status: 415
Content-Type: application/problem+json
Accept: application/xml

{
  "type": "about:blank",
  "title": "Unsupported Media Type",
  "status": 415,
  "instance": "/flux-extended-problem-detail/unsupported-media-type-status-exception"
}
```

## 4. `org.springframework.web.server.MissingRequestValueException`

_extends ServerWebInputException → ResponseStatusException_

**Request**

```text
GET /flux-extended-problem-detail/missing-request-value-exception
None (missing query parameter id)
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Required query parameter 'id' is not present.",
  "instance": "/flux-extended-problem-detail/missing-request-value-exception"
}
```

## 5. `org.springframework.web.server.UnsatisfiedRequestParameterException`

_extends ServerWebInputException → ResponseStatusException_

**Request**

```text
GET /flux-extended-problem-detail/unsatisfied-request-parameter-exception
None (does not satisfy params condition `type=1`, `exist`, `!debug`)
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request parameters.",
  "instance": "/flux-extended-problem-detail/unsatisfied-request-parameter-exception"
}
```

## 6. `org.springframework.web.bind.support.WebExchangeBindException`

_extends ServerWebInputException → ResponseStatusException_

**Request**

```text
POST /flux-extended-problem-detail/web-exchange-bind-exception
Content-Type: application/json; Body: {"name":"abc","password":"123"}
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/flux-extended-problem-detail/web-exchange-bind-exception",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "name",
      "message": "Name length must be between 6-10"
    },
    {
      "type": "PARAMETER",
      "target": "age",
      "message": "Age cannot be null"
    },
    {
      "type": "PARAMETER",
      "target": "password",
      "message": "Password and confirm password do not match"
    },
    {
      "type": "PARAMETER",
      "target": "confirmPassword",
      "message": "Password and confirm password do not match"
    }
  ]
}
```

## 7. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/handler-method-validation-exception-cookie-value
Cookie: cookieValue=
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-cookie-value",
  "errors": [
    {
      "type": "COOKIE",
      "target": "cookieValue",
      "message": "cookie cannot be empty"
    }
  ]
}
```

## 8. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/handler-method-validation-exception-matrix/abc;list=a,b,c
Path matrix: list=a,b,c
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-matrix/abc;list=a,b,c",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "list",
      "message": "list maximum size is 2"
    }
  ]
}
```

## 9. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/handler-method-validation-exception-model-attribute
None
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-model-attribute",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "password",
      "message": "Password cannot be empty"
    }
  ]
}
```

## 10. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/handler-method-validation-exception-path-variable/abc
PathVariable: id=abc
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-path-variable/abc",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "id",
      "message": "id length must be at least 5"
    }
  ]
}
```

## 11. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /flux-extended-problem-detail/handler-method-validation-exception-request-body
Content-Type: application/json; Body: {"name":"abc"}
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-request-body",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "password",
      "message": "Password cannot be empty"
    }
  ]
}
```

## 12. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /flux-extended-problem-detail/handler-method-validation-exception-request-body-validation-result
Content-Type: application/json; Body: ["","a"]
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-request-body-validation-result",
  "errors": [
    {
      "type": "PARAMETER",
      "message": "Element cannot contain empty values"
    }
  ]
}
```

## 13. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/handler-method-validation-exception-request-header
Header: headerValue=
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-request-header",
  "errors": [
    {
      "type": "HEADER",
      "target": "headerValue",
      "message": "Header cannot be empty"
    }
  ]
}
```

## 14. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/handler-method-validation-exception-request-param?param=&value=ab
Query: param=; value=ab
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-request-param",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "param",
      "message": "Parameter cannot be empty"
    },
    {
      "type": "PARAMETER",
      "target": "value",
      "message": "Length must be at least 5"
    }
  ]
}
```

## 15. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /flux-extended-problem-detail/handler-method-validation-exception-request-part
Body: {}
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-request-part",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "file",
      "message": "File cannot be empty"
    }
  ]
}
```

## 16. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/handler-method-validation-exception-other
None
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/flux-extended-problem-detail/handler-method-validation-exception-other"
}
```

## 17. `org.springframework.web.server.ServerWebInputException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/server-web-input-exception
None
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "server web input error",
  "instance": "/flux-extended-problem-detail/server-web-input-exception"
}
```

## 18. `org.springframework.web.server.ServerErrorException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/server-error-exception
None
```

**Response**

```http
status: 500
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "server error",
  "instance": "/flux-extended-problem-detail/server-error-exception"
}
```

## 19. `org.springframework.web.server.ResponseStatusException`

_extends ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/response-status-exception
None
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "exception",
  "instance": "/flux-extended-problem-detail/response-status-exception"
}
```

## 20. `org.springframework.web.reactive.resource.NoResourceFoundException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/no-resource-found
None
```

**Response**

```http
status: 404
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "No static resource flux-extended-problem-detail/no-resource-found.",
  "instance": "/flux-extended-problem-detail/no-resource-found"
}
```

## 21. `org.springframework.web.server.PayloadTooLargeException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /flux-extended-problem-detail/payload-too-large-exception
Body: text
```

**Response**

```http
status: 413
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Payload Too Large",
  "status": 413,
  "instance": "/flux-extended-problem-detail/payload-too-large-exception"
}
```

## 22. `org.springframework.web.ErrorResponseException`

**Request**

```text
GET /flux-extended-problem-detail/error-response-exception
None
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Error title",
  "status": 400,
  "detail": "Error details",
  "instance": "/flux-extended-problem-detail/error-response-exception",
  "errors": [
    {
      "type": "BUSINESS",
      "message": "Error message 1"
    },
    {
      "type": "BUSINESS",
      "message": "Error message 2"
    }
  ]
}
```

## 23. `io.github.sbracely.extended.problem.detail.webflux.example.exception.FluxExtendedErrorResponseException`

_extends ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/extended-error-response-exception
None
```

**Response**

```http
status: 500
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Payment failed title",
  "status": 500,
  "detail": "Payment failed details",
  "instance": "/flux-extended-problem-detail/extended-error-response-exception",
  "errors": [
    {
      "type": "BUSINESS",
      "message": "Insufficient balance"
    },
    {
      "type": "BUSINESS",
      "message": "Payment frequent"
    }
  ]
}
```

## 24. `org.springframework.validation.method.MethodValidationException`

**Request**

```text
GET /flux-extended-problem-detail/method-validation-exception
None
```

**Response**

```http
status: 500
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Validation failed",
  "instance": "/flux-extended-problem-detail/method-validation-exception"
}
```
