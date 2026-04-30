# WebMVC Example Application Exception Response Table

- Data source: real runtime results captured by starting/running the current WebMVC example application and triggering each scenario the same way as in the controller tests.
- Ordering: follows the current `ResponseEntityExceptionHandler#handleException` dispatch order; subclasses are placed next to their parent class.
- The 3 API-version rows are triggered with the dedicated controller-test configuration: `spring.mvc.apiversion.use.header=API-Version`, `spring.mvc.apiversion.supported=1,2`.
- Matrix-variable-related cases are listed separately in 2 rows: `MissingMatrixVariableException` (the matrix variable is truly missing) and the matrix-variable validation-failure case for `HandlerMethodValidationException`.
- The `asyncRequestNotUsableException` row records the **normal client-visible response**: `200 text/event-stream`; the exception is only triggered after the client disconnects/times out and the server keeps writing SSE data.

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
  "detail" : "Method 'POST' is not supported.",
  "instance" : "/mvc-extended-problem-detail/http-request-method-not-supported-exception",
  "status" : 405,
  "title" : "Method Not Allowed"
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
  "detail" : "Content-Type 'null' is not supported.",
  "instance" : "/mvc-extended-problem-detail/http-media-type-not-supported-exception",
  "status" : 415,
  "title" : "Unsupported Media Type"
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
  "detail" : "Acceptable representations: [application/json].",
  "instance" : "/mvc-extended-problem-detail/http-media-type-not-acceptable-exception",
  "status" : 406,
  "title" : "Not Acceptable"
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
  "detail" : "Required path variable 'id' is not present.",
  "instance" : "/mvc-extended-problem-detail/missing-path-variable-exception",
  "status" : 500,
  "title" : "Internal Server Error"
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
  "detail" : "Required parameter 'id' is not present.",
  "instance" : "/mvc-extended-problem-detail/missing-servlet-request-parameter-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Required part 'file' is not present.",
  "instance" : "/mvc-extended-problem-detail/missing-servlet-request-part-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "instance" : "/mvc-extended-problem-detail/servlet-request-binding-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Invalid request parameters.",
  "instance" : "/mvc-extended-problem-detail/unsatisfied-servlet-request-parameter-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "instance" : "/mvc-extended-problem-detail/org-spring-web-bind-missing-request-value-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Required path parameter 'list' is not present.",
  "instance" : "/mvc-extended-problem-detail/missing-matrix-variable-exception/abc;list1=a,b,c",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Required cookie 'cookieValue' is not present.",
  "instance" : "/mvc-extended-problem-detail/missing-request-cookie-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Required header 'header' is not present.",
  "instance" : "/mvc-extended-problem-detail/missing-request-header-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 13. `org.springframework.web.bind.MethodArgumentNotValidException`

**Request**

```text
POST /mvc-extended-problem-detail/method-argument-not-valid-exception
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
      "target" : "age",
      "message" : "Age cannot be null"
    },
    {
      "type" : "PARAMETER",
      "target" : "password",
      "message" : "Password and confirm password do not match"
    },
    {
      "type" : "PARAMETER",
      "target" : "confirmPassword",
      "message" : "Password and confirm password do not match"
    },
    {
      "type" : "PARAMETER",
      "target" : "name",
      "message" : "Name length must be between 6-10"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/method-argument-not-valid-exception",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "COOKIE",
      "target" : "name",
      "message" : "Name length must be at least 2"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-cookie-value",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "list",
      "message" : "Maximum size is 2"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-matrix-variable/abc;list=a,b,c",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "password",
      "message" : "Password cannot be empty"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-model-attribute",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "id",
      "message" : "ID minimum length is 2"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-path-variable/a",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 18. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /mvc-extended-problem-detail/handler-method-validation-exception-request-body
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
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-request-body",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 19. `org.springframework.web.method.annotation.HandlerMethodValidationException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result
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
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "HEADER",
      "target" : "headerValue",
      "message" : "Minimum length is 2"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-request-header",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "param",
      "message" : "Parameter cannot be empty"
    },
    {
      "type" : "PARAMETER",
      "target" : "param2",
      "message" : "Parameter 2 cannot be null"
    },
    {
      "type" : "PARAMETER",
      "target" : "param2",
      "message" : "Parameter 2 cannot be blank"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-request-param",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "errors" : [
    {
      "type" : "PARAMETER",
      "target" : "file",
      "message" : "File cannot be empty"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-request-part",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "Validation failure",
  "instance" : "/mvc-extended-problem-detail/handler-method-validation-exception-other",
  "status" : 400,
  "title" : "Bad Request"
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
  "detail" : "No endpoint GET /mvc-extended-problem-detail/no-handler-found-exception.",
  "instance" : "/mvc-extended-problem-detail/no-handler-found-exception",
  "status" : 404,
  "title" : "Not Found"
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
  "detail" : "No static resource mvc-extended-problem-detail/no-resource-found-exception.",
  "instance" : "/mvc-extended-problem-detail/no-resource-found-exception",
  "status" : 404,
  "title" : "Not Found"
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
  "instance" : "/mvc-extended-problem-detail/async-request-timeout-exception",
  "status" : 503,
  "title" : "Service Unavailable"
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
  "instance" : "/mvc-extended-problem-detail/error-response-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 28. `io.github.sbracely.extended.problem.detail.webmvc.example.exception.PayFailedException`

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
  "detail" : "Payment failed",
  "errors" : [
    {
      "type" : "BUSINESS",
      "target" : null,
      "message" : "Insufficient balance"
    },
    {
      "type" : "BUSINESS",
      "target" : null,
      "message" : "Payment frequent"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/extended-error-response-exception",
  "status" : 500,
  "title" : "Internal Server Error"
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
Allow: GET,POST

{
  "detail" : "Supported methods: [GET, POST]",
  "instance" : "/mvc-extended-problem-detail/method-not-allowed-exception",
  "status" : 405,
  "title" : "Method Not Allowed"
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
  "detail" : "Acceptable representations: [application/json].",
  "instance" : "/mvc-extended-problem-detail/not-acceptable-status-exception",
  "status" : 406,
  "title" : "Not Acceptable"
}
```

## 31. `org.springframework.web.server.ContentTooLargeException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
POST /mvc-extended-problem-detail/content-too-large-exception
multipart/form-data; upload `file=test.txt`
```

**Response**

```http
status: 413
Content-Type: application/problem+json

{
  "instance" : "/mvc-extended-problem-detail/content-too-large-exception",
  "status" : 413,
  "title" : "Content Too Large"
}
```

## 32. `org.springframework.web.server.UnsupportedMediaTypeStatusException`

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
  "detail" : "Could not parse Content-Type.",
  "instance" : "/mvc-extended-problem-detail/unsupported-media-type-status-exception",
  "status" : 415,
  "title" : "Unsupported Media Type"
}
```

## 33. `org.springframework.web.server.ResponseStatusException`

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
  "detail" : "exception",
  "instance" : "/mvc-extended-problem-detail/response-status-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 34. `org.springframework.boot.webmvc.actuate.endpoint.web
      .AbstractWebMvcEndpointHandlerMapping.InvalidEndpointBadRequestException`

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
  "detail" : "Missing parameters: param1,param2",
  "instance" : "/actuator/demo/name",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 35. `org.springframework.web.server.ServerWebInputException`

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
  "detail" : "server web input error",
  "instance" : "/mvc-extended-problem-detail/server-web-input-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 36. `org.springframework.web.bind.support.WebExchangeBindException`

_extends ServerWebInputException → ResponseStatusException_

**Request**

```text
POST /mvc-extended-problem-detail/web-exchange-bind-exception
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
      "target" : "age",
      "message" : "Age cannot be null"
    },
    {
      "type" : "PARAMETER",
      "target" : "password",
      "message" : "Password and confirm password do not match"
    },
    {
      "type" : "PARAMETER",
      "target" : "confirmPassword",
      "message" : "Password and confirm password do not match"
    }
  ],
  "instance" : "/mvc-extended-problem-detail/web-exchange-bind-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 37. `org.springframework.web.server.MissingRequestValueException`

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
  "detail" : "Required request param 'id' is not present.",
  "instance" : "/mvc-extended-problem-detail/org-springframework-web-server-missing-request-value-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 38. `org.springframework.web.server.UnsatisfiedRequestParameterException`

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
  "detail" : "Invalid request parameters.",
  "instance" : "/mvc-extended-problem-detail/unsatisfied-request-parameter-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 39. `org.springframework.web.server.ServerErrorException`

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
  "detail" : "server error",
  "instance" : "/mvc-extended-problem-detail/server-error-exception",
  "status" : 500,
  "title" : "Internal Server Error"
}
```

## 40. `org.springframework.web.server.PayloadTooLargeException`

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
  "detail" : "payload too large",
  "instance" : "/mvc-extended-problem-detail/payload-too-large-exception",
  "status" : 413,
  "title" : "Content Too Large"
}
```

## 41. `org.springframework.web.accept.InvalidApiVersionException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/invalid-api-version-exception
Header: API-Version=3; requires `spring.mvc.apiversion.*`
```

**Response**

```http
status: 400
Content-Type: application/problem+json
connection: close
date: Fri, 10 Apr 2026 20:45:01 GMT
transfer-encoding: chunked

{
  "detail" : "Invalid API version: '3.0.0'.",
  "instance" : "/mvc-extended-problem-detail/invalid-api-version-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 42. `org.springframework.web.accept.MissingApiVersionException`

_extends ResponseStatusException → ErrorResponseException_

**Request**

```text
GET /mvc-extended-problem-detail/missing-api-version-exception
API-Version not sent; requires `spring.mvc.apiversion.*`
```

**Response**

```http
status: 400
Content-Type: application/problem+json
connection: close
date: Fri, 10 Apr 2026 20:45:01 GMT
transfer-encoding: chunked

{
  "detail" : "API version is required.",
  "instance" : "/mvc-extended-problem-detail/missing-api-version-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 43. `org.springframework.web.accept.NotAcceptableApiVersionException`

_extends InvalidApiVersionException → ResponseStatusException_

**Request**

```text
GET /not-acceptable-api-version
Header: API-Version=2; requires `spring.mvc.apiversion.*`
```

**Response**

```http
status: 400
Content-Type: application/problem+json
connection: close
date: Fri, 10 Apr 2026 20:45:01 GMT
transfer-encoding: chunked

{
  "detail" : "Invalid API version: '2.0.0'.",
  "instance" : "/not-acceptable-api-version",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 44. `org.springframework.web.multipart.MaxUploadSizeExceededException`

**Request**

```text
POST /mvc-extended-problem-detail/max-upload-size-exceeded-exception
multipart/form-data; upload a 2-byte file; requires `spring.servlet.multipart.max-file-size=1`
```

**Response**

```http
status: 413
Content-Type: application/problem+json
connection: close
date: Fri, 10 Apr 2026 20:45:01 GMT
transfer-encoding: chunked

{
  "detail" : "Maximum upload size exceeded",
  "instance" : "/mvc-extended-problem-detail/max-upload-size-exceeded-exception",
  "status" : 413,
  "title" : "Content Too Large"
}
```

## 45. `org.springframework.beans.ConversionNotSupportedException`

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
  "detail" : "Failed to convert 'null' with value: 'test-value'",
  "instance" : "/mvc-extended-problem-detail/conversion-not-supported-exception",
  "status" : 500,
  "title" : "Internal Server Error"
}
```

## 46. `org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException`

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
  "detail" : "Failed to convert 'error' with value: 'test-value'",
  "instance" : "/mvc-extended-problem-detail/method-argument-conversion-not-supported-exception",
  "status" : 500,
  "title" : "Internal Server Error"
}
```

## 47. `org.springframework.beans.TypeMismatchException`

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
  "detail" : "Failed to convert 'null' with value: 'test'",
  "instance" : "/mvc-extended-problem-detail/type-mismatch-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 48. `org.springframework.web.method.annotation.MethodArgumentTypeMismatchException`

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
  "detail" : "Failed to convert 'integer' with value: 'a'",
  "instance" : "/mvc-extended-problem-detail/method-argument-type-mismatch-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 49. `org.springframework.http.converter.HttpMessageNotReadableException`

**Request**

```text
POST /mvc-extended-problem-detail/http-message-not-readable-exception
Content-Type: application/json；Body: {
```

**Response**

```http
status: 400
Content-Type: application/problem+json

{
  "detail" : "Failed to read request",
  "instance" : "/mvc-extended-problem-detail/http-message-not-readable-exception",
  "status" : 400,
  "title" : "Bad Request"
}
```

## 50. `org.springframework.http.converter.HttpMessageNotWritableException`

**Request**

```text
GET /mvc-extended-problem-detail/http-message-not-writable-exception
None
```

**Response**

```http
status: 500
Content-Type: application/problem+json

{
  "detail" : "Failed to write request",
  "instance" : "/mvc-extended-problem-detail/http-message-not-writable-exception",
  "status" : 500,
  "title" : "Internal Server Error"
}
```

## 51. `org.springframework.validation.method.MethodValidationException`

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
  "detail" : "Validation failed",
  "instance" : "/mvc-extended-problem-detail/method-validation-exception",
  "status" : 500,
  "title" : "Internal Server Error"
}
```

## 52. `org.springframework.web.context.request.async.AsyncRequestNotUsableException`

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

data:event 3

data:event 4

data:event 5

... (the stream keeps sending data; `AsyncRequestNotUsableException` is only triggered after the client times out/disconnects and the server writes again)
```
