# WebMVC Example Application Exception Response Table

- Data source: response bodies come from the current `3.x` branch `openapi.json` examples that are kept aligned with runtime by contract tests; controller-only scenarios are added from the current controller tests.
- Ordering: follows the current `ResponseEntityExceptionHandler#handleException` dispatch order on the `3.x` line; subclasses are placed next to their parent class.
- Matrix-variable-related cases are listed separately in 2 rows: `MissingMatrixVariableException` and the matrix-variable validation-failure case for `HandlerMethodValidationException`.
- `asyncRequestNotUsableException` records the normal client-visible response (`200 text/event-stream`); the exception itself is only triggered after the client disconnects/times out and the server writes again.

## 1. `org.springframework.web.HttpRequestMethodNotSupportedException`

**Request**

```text
POST /mvc-extended-problem-detail/http-request-method-not-supported-exception
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
  "detail": "Method 'POST' is not supported.",
  "instance": "/mvc-extended-problem-detail/http-request-method-not-supported-exception"
}
```

## 2. `org.springframework.web.HttpMediaTypeNotSupportedException`

**Request**

```text
PUT /mvc-extended-problem-detail/http-media-type-not-supported-exception
None (no Content-Type sent)
```

**Response**

```http
status: 415
Content-Type: application/problem+json
Accept: application/json

{
  "type": "about:blank",
  "title": "Unsupported Media Type",
  "status": 415,
  "detail": "Content-Type 'null' is not supported.",
  "instance": "/mvc-extended-problem-detail/http-media-type-not-supported-exception"
}
```

## 3. `org.springframework.web.HttpMediaTypeNotAcceptableException`

**Request**

```text
PUT /mvc-extended-problem-detail/http-media-type-not-acceptable-exception
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
  "instance": "/mvc-extended-problem-detail/http-media-type-not-acceptable-exception"
}
```

## 4. `org.springframework.web.bind.MissingPathVariableException`

**Request**

```text
DELETE /mvc-extended-problem-detail/missing-path-variable-exception
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
  "detail": "Required path variable 'id' is not present.",
  "instance": "/mvc-extended-problem-detail/missing-path-variable-exception"
}
```

## 5. `org.springframework.web.bind.MissingServletRequestParameterException`

**Request**

```text
GET /mvc-extended-problem-detail/missing-servlet-request-parameter-exception
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
  "detail": "Required parameter 'id' is not present.",
  "instance": "/mvc-extended-problem-detail/missing-servlet-request-parameter-exception"
}
```

## 6. `org.springframework.web.multipart.support.MissingServletRequestPartException`

**Request**

```text
PUT /mvc-extended-problem-detail/missing-servlet-request-part-exception
multipart/form-data; missing file part
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Required part 'file' is not present.",
  "instance": "/mvc-extended-problem-detail/missing-servlet-request-part-exception"
}
```

## 7. `org.springframework.web.bind.ServletRequestBindingException`

**Request**

```text
GET /mvc-extended-problem-detail/servlet-request-binding-exception
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
  "instance": "/mvc-extended-problem-detail/servlet-request-binding-exception"
}
```

## 8. `org.springframework.web.bind.UnsatisfiedServletRequestParameterException`

_extends ServletRequestBindingException_

**Request**

```text
GET /mvc-extended-problem-detail/unsatisfied-servlet-request-parameter-exception
Query: type=1 (does not satisfy exist and !debug)
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
  "instance": "/mvc-extended-problem-detail/unsatisfied-servlet-request-parameter-exception"
}
```

## 9. `org.springframework.web.bind.MissingRequestValueException`

_extends ServletRequestBindingException_

**Request**

```text
GET /mvc-extended-problem-detail/org-spring-web-bind-missing-request-value-exception
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
  "instance": "/mvc-extended-problem-detail/org-spring-web-bind-missing-request-value-exception"
}
```

## 10. `org.springframework.web.bind.MissingMatrixVariableException`

_extends MissingRequestValueException → ServletRequestBindingException_

**Request**

```text
GET /mvc-extended-problem-detail/missing-matrix-variable-exception/abc;list1=a,b,c
Path matrix: list1=a,b,c (intentionally missing list)
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Required path parameter 'list' is not present.",
  "instance": "/mvc-extended-problem-detail/missing-matrix-variable-exception/abc;list1=a,b,c"
}
```

## 11. `org.springframework.web.bind.MissingRequestCookieException`

_extends MissingRequestValueException → ServletRequestBindingException_

**Request**

```text
GET /mvc-extended-problem-detail/missing-request-cookie-exception
None (missing cookie `cookieValue`)
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Required cookie 'cookieValue' is not present.",
  "instance": "/mvc-extended-problem-detail/missing-request-cookie-exception"
}
```

## 12. `org.springframework.web.bind.MissingRequestHeaderException`

_extends MissingRequestValueException → ServletRequestBindingException_

**Request**

```text
GET /mvc-extended-problem-detail/missing-request-header-exception
None (missing request header `header`)
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Required header 'header' is not present.",
  "instance": "/mvc-extended-problem-detail/missing-request-header-exception"
}
```

## 13. `org.springframework.web.bind.MethodArgumentNotValidException`

**Request**

```text
POST /mvc-extended-problem-detail/method-argument-not-valid-exception
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
  "instance": "/mvc-extended-problem-detail/method-argument-not-valid-exception",
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

## 14. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/handler-method-validation-exception-cookie-value
Cookie: name=a
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-cookie-value",
  "errors": [
    {
      "type": "COOKIE",
      "target": "name",
      "message": "Name length must be at least 2"
    }
  ]
}
```

## 15. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/handler-method-validation-exception-matrix-variable/abc;list=a,b,c
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-matrix-variable/abc;list=a,b,c",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "list",
      "message": "Maximum size is 2"
    }
  ]
}
```

## 16. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/handler-method-validation-exception-model-attribute
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-model-attribute",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "password",
      "message": "Password cannot be empty"
    }
  ]
}
```

## 17. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/handler-method-validation-exception-path-variable/a
PathVariable: id=a
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-path-variable/a",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "id",
      "message": "ID minimum length is 2"
    }
  ]
}
```

## 18. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /mvc-extended-problem-detail/handler-method-validation-exception-request-body
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-body",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "password",
      "message": "Password cannot be empty"
    }
  ]
}
```

## 19. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result",
  "errors": [
    {
      "type": "PARAMETER",
      "message": "Element cannot contain empty values"
    }
  ]
}
```

## 20. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/handler-method-validation-exception-request-header
Header: headerValue=a
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-header",
  "errors": [
    {
      "type": "HEADER",
      "target": "headerValue",
      "message": "Minimum length is 2"
    }
  ]
}
```

## 21. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/handler-method-validation-exception-request-param
None (intentionally missing `param` and `param2`)
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-param",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "param",
      "message": "Parameter cannot be empty"
    },
    {
      "type": "PARAMETER",
      "target": "param2",
      "message": "Parameter 2 cannot be null"
    },
    {
      "type": "PARAMETER",
      "target": "param2",
      "message": "Parameter 2 cannot be blank"
    }
  ]
}
```

## 22. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/handler-method-validation-exception-request-part
Header: Content-Type=multipart/form-data
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-part",
  "errors": [
    {
      "type": "PARAMETER",
      "target": "file",
      "message": "File cannot be empty"
    }
  ]
}
```

## 23. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/handler-method-validation-exception-other
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
  "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-other"
}
```

## 24. `org.springframework.web.servlet.NoHandlerFoundException`

**Request**

```text
GET /mvc-extended-problem-detail/no-handler-found-exception
None (requires `spring.web.resources.add-mappings=false`)
```

**Response**

```http
status: 404
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "No static resource mvc-extended-problem-detail/no-handler-found-exception.",
  "instance": "/mvc-extended-problem-detail/no-handler-found-exception"
}
```

## 25. `org.springframework.web.servlet.resource.NoResourceFoundException`

**Request**

```text
GET /mvc-extended-problem-detail/no-resource-found-exception
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
  "detail": "No static resource mvc-extended-problem-detail/no-resource-found-exception.",
  "instance": "/mvc-extended-problem-detail/no-resource-found-exception"
}
```

## 26. `org.springframework.web.context.request.async.AsyncRequestTimeoutException`

**Request**

```text
GET /mvc-extended-problem-detail/async-request-timeout-exception
None (async timeout is triggered intentionally in the test)
```

**Response**

```http
status: 503
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Service Unavailable",
  "status": 503,
  "instance": "/mvc-extended-problem-detail/async-request-timeout-exception"
}
```

## 27. `org.springframework.web.ErrorResponseException`

**Request**

```text
GET /mvc-extended-problem-detail/error-response-exception
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
  "instance": "/mvc-extended-problem-detail/error-response-exception"
}
```

## 28. `io.github.sbracely.extended.problem.detail.webmvc.example.exception.MvcExtendedErrorResponseException`

_extends ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/extended-error-response-exception
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
  "detail": "Payment failed",
  "instance": "/mvc-extended-problem-detail/extended-error-response-exception",
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

## 29. `org.springframework.web.server.MethodNotAllowedException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
DELETE /mvc-extended-problem-detail/method-not-allowed-exception
None
```

**Response**

```http
status: 405
Content-Type: application/problem+json
Allow: GET, POST

{
  "type": "about:blank",
  "title": "Method Not Allowed",
  "status": 405,
  "detail": "Supported methods: [GET, POST]",
  "instance": "/mvc-extended-problem-detail/method-not-allowed-exception"
}
```

## 30. `org.springframework.web.server.NotAcceptableStatusException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/not-acceptable-status-exception
None
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
  "instance": "/mvc-extended-problem-detail/not-acceptable-status-exception"
}
```

## 31. `org.springframework.web.server.UnsupportedMediaTypeStatusException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /mvc-extended-problem-detail/unsupported-media-type-status-exception
None (no valid Content-Type sent)
```

**Response**

```http
status: 415
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Unsupported Media Type",
  "status": 415,
  "detail": "Could not parse Content-Type.",
  "instance": "/mvc-extended-problem-detail/unsupported-media-type-status-exception"
}
```

## 32. `org.springframework.web.server.ResponseStatusException`

_extends ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/response-status-exception
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
  "instance": "/mvc-extended-problem-detail/response-status-exception"
}
```

## 33. `InvalidEndpointBadRequestException`

_extends ResponseStatusException_

**Request**

```text
GET /actuator/demo/name
None (requires `management.endpoints.web.exposure.include=demo`)
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Missing parameters: param1,param2",
  "instance": "/actuator/demo/name"
}
```

## 34. `org.springframework.web.server.ServerWebInputException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/server-web-input-exception
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
  "instance": "/mvc-extended-problem-detail/server-web-input-exception"
}
```

## 35. `org.springframework.web.server.MissingRequestValueException`

_extends ServerWebInputException → ResponseStatusException_

**Request**

```text
GET /mvc-extended-problem-detail/org-springframework-web-server-missing-request-value-exception
None (missing request parameter `id`)
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Required request param 'id' is not present.",
  "instance": "/mvc-extended-problem-detail/org-springframework-web-server-missing-request-value-exception"
}
```

## 36. `org.springframework.web.server.UnsatisfiedRequestParameterException`

_extends ServerWebInputException → ResponseStatusException_

**Request**

```text
GET /mvc-extended-problem-detail/unsatisfied-request-parameter-exception
Query: type=1 (does not satisfy exist and !debug)
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
  "instance": "/mvc-extended-problem-detail/unsatisfied-request-parameter-exception"
}
```

## 37. `org.springframework.web.bind.support.WebExchangeBindException`

_extends ServerWebInputException → ResponseStatusException_

**Request**

```text
POST /mvc-extended-problem-detail/web-exchange-bind-exception
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
  "instance": "/mvc-extended-problem-detail/web-exchange-bind-exception",
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

## 38. `org.springframework.web.server.ServerErrorException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/server-error-exception
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
  "instance": "/mvc-extended-problem-detail/server-error-exception"
}
```

## 39. `org.springframework.web.server.PayloadTooLargeException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /mvc-extended-problem-detail/payload-too-large-exception
multipart/form-data; upload `file=test.txt`
```

**Response**

```http
status: 413
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Payload Too Large",
  "status": 413,
  "detail": "payload too large",
  "instance": "/mvc-extended-problem-detail/payload-too-large-exception"
}
```

## 40. `org.springframework.web.multipart.MaxUploadSizeExceededException`

**Request**

```text
POST /mvc-extended-problem-detail/max-upload-size-exceeded-exception
multipart/form-data; upload a 2-byte file; requires `spring.servlet.multipart.max-file-size=1`
```

**Response**

```http
status: 413
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Payload Too Large",
  "status": 413,
  "detail": "Maximum upload size exceeded",
  "instance": "/mvc-extended-problem-detail/max-upload-size-exceeded-exception"
}
```

## 41. `org.springframework.beans.ConversionNotSupportedException`

**Request**

```text
GET /mvc-extended-problem-detail/conversion-not-supported-exception
Query: data=test-value
```

**Response**

```http
status: 500
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Failed to convert 'null' with value: 'test-value'",
  "instance": "/mvc-extended-problem-detail/conversion-not-supported-exception"
}
```

## 42. `org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException`

_extends ConversionNotSupportedException → TypeMismatchException_

**Request**

```text
GET /mvc-extended-problem-detail/method-argument-conversion-not-supported-exception
Query: error=test-value
```

**Response**

```http
status: 500
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Failed to convert 'error' with value: 'test-value'",
  "instance": "/mvc-extended-problem-detail/method-argument-conversion-not-supported-exception"
}
```

## 43. `org.springframework.beans.TypeMismatchException`

**Request**

```text
GET /mvc-extended-problem-detail/type-mismatch-exception
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
  "detail": "Failed to convert 'null' with value: 'test'",
  "instance": "/mvc-extended-problem-detail/type-mismatch-exception"
}
```

## 44. `org.springframework.web.method.annotation.MethodArgumentTypeMismatchException`

_extends TypeMismatchException_

**Request**

```text
GET /mvc-extended-problem-detail/method-argument-type-mismatch-exception
Query: integer=a
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Failed to convert 'integer' with value: 'a'",
  "instance": "/mvc-extended-problem-detail/method-argument-type-mismatch-exception"
}
```

## 45. `org.springframework.http.converter.HttpMessageNotReadableException`

**Request**

```text
POST /mvc-extended-problem-detail/http-message-not-readable-exception
Content-Type: application/json; Body: {
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Failed to read request",
  "instance": "/mvc-extended-problem-detail/http-message-not-readable-exception"
}
```

## 46. `org.springframework.http.converter.HttpMessageNotWritableException`

**Request**

```text
GET /mvc-extended-problem-detail/http-message-not-writable-exception
None
```

**Response**

```http
status: 500
Content-Type: application/json

{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Failed to write request",
  "instance": "/mvc-extended-problem-detail/http-message-not-writable-exception"
}
```

## 47. `org.springframework.validation.method.MethodValidationException`

**Request**

```text
GET /mvc-extended-problem-detail/method-validation-exception
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
  "instance": "/mvc-extended-problem-detail/method-validation-exception"
}
```

## 48. `org.springframework.web.context.request.async.AsyncRequestNotUsableException`

**Request**

```text
GET /mvc-extended-problem-detail/async-request-not-usable-exception
Header: Accept=text/event-stream
```

**Response**

```http
status: 200
Content-Type: text/event-stream

data:event 0

data:event 1

data:event 2

... (the stream normally keeps sending data; AsyncRequestNotUsableException is only triggered after the client times out/disconnects and the server writes again)
```
