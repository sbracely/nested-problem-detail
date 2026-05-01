# WebFlux Example Application Exception Response Table

- Data source: real responses captured by starting the current WebFlux example application and sending real requests.
- Ordering: follows the current `ResponseEntityExceptionHandler#handleException` dispatch order; subclasses are placed next to their parent class.
- The 3 API-version rows are triggered with the API-version configuration: `spring.webflux.apiversion.use.header=API-Version`, `spring.webflux.apiversion.supported=1,2`; `/not-acceptable-api-version` is registered by `FluxApiVersionController` only when that configuration is enabled.

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
  "detail" : "Supported methods: [GET]",
  "instance" : "/flux-extended-problem-detail/method-not-allowed-exception",
  "status" : 405,
  "title" : "Method Not Allowed"
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
  "detail" : "Acceptable representations: [application/json].",
  "instance" : "/flux-extended-problem-detail/not-acceptable-status-exception",
  "status" : 406,
  "title" : "Not Acceptable"
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
  "instance" : "/flux-extended-problem-detail/unsupported-media-type-status-exception",
  "status" : 415,
  "title" : "Unsupported Media Type"
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
  "detail" : "Required query parameter 'id' is not present.",
  "instance" : "/flux-extended-problem-detail/missing-request-value-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Invalid request parameters.",
  "instance" : "/flux-extended-problem-detail/unsatisfied-request-parameter-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 6. `org.springframework.web.bind.support.WebExchangeBindException`

_extends ServerWebInputException → ResponseStatusException_

**Request**

```text
POST /flux-extended-problem-detail/web-exchange-bind-exception
Content-Type: application/json；Body: {"name":"abc","password":"123"}
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "detail" : "Invalid request content.",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "name",
      "message" : "Name length must be between 6-10"
    },
    {
      "type" : "PARAMETER",
      "target" : "confirmPassword",
      "message" : "Password and confirm password do not match"
    },
    {
      "type" : "PARAMETER",
      "target" : "password",
      "message" : "Password and confirm password do not match"
    },
    {
      "type" : "PARAMETER",
      "target" : "age",
      "message" : "Age cannot be null"
    }
  ],
  "instance" : "/flux-extended-problem-detail/web-exchange-bind-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "COOKIE",
      "target" : "cookieValue",
      "message" : "cookie cannot be empty"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-cookie-value",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "list",
      "message" : "list maximum size is 2"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-matrix/abc;list=a,b,c",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "password",
      "message" : "Password cannot be empty"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-model-attribute",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "id",
      "message" : "id length must be at least 5"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-path-variable/abc",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 11. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /flux-extended-problem-detail/handler-method-validation-exception-request-body
Content-Type: application/json；Body: {"name":"abc"}
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "password",
      "message" : "Password cannot be empty"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-request-body",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 12. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /flux-extended-problem-detail/handler-method-validation-exception-request-body-validation-result
Content-Type: application/json；Body: ["","a"]
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : null,
      "message" : "Element cannot contain empty values"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-request-body-validation-result",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "HEADER",
      "target" : "headerValue",
      "message" : "Header cannot be empty"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-request-header",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 14. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/handler-method-validation-exception-request-param?param=&value=ab
Query: param=；value=ab
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "param",
      "message" : "Parameter cannot be empty"
    },
    {
      "type" : "PARAMETER",
      "target" : "value",
      "message" : "Length must be at least 5"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-request-param",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "file",
      "message" : "File cannot be empty"
    }
  ],
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-request-part",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "instance" : "/flux-extended-problem-detail/handler-method-validation-exception-other",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "server web input error",
  "instance" : "/flux-extended-problem-detail/server-web-input-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "server error",
  "instance" : "/flux-extended-problem-detail/server-error-exception",
  "status" : 500,
  "title" : "Internal Server Error"
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
  "detail" : "exception",
  "instance" : "/flux-extended-problem-detail/response-status-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 20. `org.springframework.web.server.ContentTooLargeException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /flux-extended-problem-detail/content-too-large-exception
Body: "x" × 1048576
```

**Response**

```http
status: 413
Content-Type: application/problem+json

{
  "instance" : "/flux-extended-problem-detail/content-too-large-exception",
  "status" : 413,
  "title" : "Content Too Large"
}
```

## 21. `org.springframework.web.accept.InvalidApiVersionException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/invalid-api-version-exception
Header: API-Version=3; requires `spring.webflux.apiversion.*`
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "detail" : "Invalid API version: '3.0.0'.",
  "instance" : "/flux-extended-problem-detail/invalid-api-version-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 22. `org.springframework.web.accept.MissingApiVersionException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /flux-extended-problem-detail/missing-api-version-exception
Requires `spring.webflux.apiversion.*`; API-Version not sent
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "detail" : "API version is required.",
  "instance" : "/flux-extended-problem-detail/missing-api-version-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 23. `org.springframework.web.accept.NotAcceptableApiVersionException`

_extends InvalidApiVersionException → ResponseStatusException_

**Request**

```text
GET /not-acceptable-api-version
Header: API-Version=2; requires `spring.webflux.apiversion.*`
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "detail" : "Invalid API version: '2.0.0'.",
  "instance" : "/not-acceptable-api-version",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 24. `org.springframework.web.reactive.resource.NoResourceFoundException`

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
  "detail" : "No static resource flux-extended-problem-detail/no-resource-found.",
  "instance" : "/flux-extended-problem-detail/no-resource-found",
  "status" : 404,
  "title" : "Not Found"
}
```

## 25. `org.springframework.web.server.PayloadTooLargeException`

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
  "instance" : "/flux-extended-problem-detail/payload-too-large-exception",
  "status" : 413,
  "title" : "Content Too Large"
}
```

## 26. `org.springframework.web.ErrorResponseException`

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
  "detail" : "Error details",
  "errors" : [
    {
      "type" : "BUSINESS",
      "target" : null,
      "message" : "Error message 1"
    },
    {
      "type" : "BUSINESS",
      "target" : null,
      "message" : "Error message 2"
    }
  ],
  "instance" : "/flux-extended-problem-detail/error-response-exception",
  "status" : 400,
  "title" : "Error title"
}
```

## 27. `io.github.sbracely.extended.problem.detail.webflux.example.exception.PayFailedException`

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
  "detail" : "The payment request could not be processed.",
  "errors" : [
    {
      "type" : "BUSINESS",
      "target" : null,
      "message" : "Insufficient balance"
    },
    {
      "type" : "BUSINESS",
      "target" : null,
      "message" : "Payment is too frequent"
    }
  ],
  "instance" : "/flux-extended-problem-detail/extended-error-response-exception",
  "status" : 500,
  "title" : "Payment failed"
}
```

## 28. `org.springframework.validation.method.MethodValidationException`

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
  "detail" : "Validation failed",
  "instance" : "/flux-extended-problem-detail/method-validation-exception",
  "status" : 500,
  "title" : "Internal Server Error"
}
```
