package io.github.sbracely.extended.problem.detail.webmvc.example.controller;

import io.github.sbracely.extended.problem.detail.mvc.MvcExtendedProblemDetailProperties;
import io.github.sbracely.extended.problem.detail.webmvc.example.config.MvcMethodValidationConfiguration;
import io.github.sbracely.extended.problem.detail.webmvc.example.exception.PayFailedException;
import io.github.sbracely.extended.problem.detail.webmvc.example.request.MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.response.MvcProblemDetailResponse;
import io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer.MvcProblemDetailResponseSerializer;
import io.github.sbracely.extended.problem.detail.webmvc.example.service.MvcProblemDetailService;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.MvcCheckMultipartFile;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.MvcCheckPassword;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.*;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/mvc-extended-problem-detail")
@Tag(name = "WebMVC Example", description = "Endpoints that intentionally trigger different MVC and validation exceptions.")
public class MvcProblemDetailController {

    private static final Logger logger = LoggerFactory.getLogger(MvcProblemDetailController.class);
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final MvcProblemDetailService problemDetailService;
    private final String errorsPropertyName;

    public MvcProblemDetailController(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                      MvcProblemDetailService problemDetailService,
                                      MvcExtendedProblemDetailProperties properties) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.problemDetailService = problemDetailService;
        this.errorsPropertyName = properties.getErrorsPropertyName();
    }

    /**
     * @see HttpRequestMethodNotSupportedException
     */
    @ApiResponse(
            responseCode = "405",
            description = "HttpRequestMethodNotSupportedException",
            headers = @Header(name = "Allow", schema = @Schema(type = "string", example = "GET")),
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Method Not Allowed",
                              "status": 405,
                              "detail": "Method 'POST' is not supported.",
                              "instance": "/mvc-extended-problem-detail/http-request-method-not-supported-exception"
                            }
                            """)))
    @GetMapping("/http-request-method-not-supported-exception")
    public void httpRequestMethodNotSupportedException() {
        logger.info("httpRequestMethodNotSupportedException");
    }

    /**
     * @see HttpMediaTypeNotSupportedException
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(name = "example",
                    value = """
                            {
                              "name": "springdoc",
                              "age": 28,
                              "password": "password123",
                              "confirmPassword": "password123"
                            }
                            """)))
    @ApiResponse(
            responseCode = "415",
            description = "HttpMediaTypeNotSupportedException",
            headers = @Header(name = "Accept", schema = @Schema(type = "string", example = MediaType.APPLICATION_JSON_VALUE)),
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Unsupported Media Type",
                              "status": 415,
                              "detail": "Content-Type 'null' is not supported.",
                              "instance": "/mvc-extended-problem-detail/http-media-type-not-supported-exception"
                            }
                            """)))
    @PutMapping(path = "/http-media-type-not-supported-exception", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void httpMediaTypeNotSupportedException(MvcProblemDetailRequest problemDetailRequest) {
        logger.info("httpMediaTypeNotSupportedException, problemDetailRequest: " + problemDetailRequest);
    }

    /**
     * @see HttpMediaTypeNotAcceptableException
     */
    @Operation(parameters = @Parameter(name = "Accept", in = ParameterIn.HEADER, example = "application/xml"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(name = "example",
                    value = """
                            {
                              "name": "springdoc",
                              "age": 28,
                              "password": "password123",
                              "confirmPassword": "password123"
                            }
                            """)))
    @ApiResponse(
            responseCode = "406",
            description = "HttpMediaTypeNotAcceptableException",
            headers = @Header(name = "Accept", schema = @Schema(type = "string", example = MediaType.APPLICATION_JSON_VALUE)),
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Not Acceptable",
                              "status": 406,
                              "detail": "Acceptable representations: [application/json].",
                              "instance": "/mvc-extended-problem-detail/http-media-type-not-acceptable-exception"
                            }
                            """)))
    @PutMapping(path = "/http-media-type-not-acceptable-exception", produces = MediaType.APPLICATION_JSON_VALUE)
    public void httpMediaTypeNotAcceptableException(MvcProblemDetailRequest problemDetailRequest) {
        logger.info("httpMediaTypeNotAcceptableException, problemDetailRequest: " + problemDetailRequest);
    }

    /**
     * @see MissingPathVariableException
     */
    @ApiResponse(
            responseCode = "500",
            description = "MissingPathVariableException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Required path variable 'id' is not present.",
                              "instance": "/mvc-extended-problem-detail/missing-path-variable-exception"
                            }
                            """)))
    @DeleteMapping("/missing-path-variable-exception")
    public void missingPathVariableException(@Parameter(example = "1") @PathVariable Integer id) {
        logger.info("missingPathVariableException, id: " + id);
    }

    /**
     * @see MissingServletRequestParameterException
     */
    @ApiResponse(
            responseCode = "400",
            description = "MissingServletRequestParameterException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Required parameter 'id' is not present.",
                              "instance": "/mvc-extended-problem-detail/missing-servlet-request-parameter-exception"
                            }
                            """)))
    @GetMapping("/missing-servlet-request-parameter-exception")
    public void missingServletRequestParameterException(@Parameter(example = "1") @RequestParam Integer id) {
        logger.info("missingServletRequestParameterException, id: " + id);
    }

    /**
     * @see MissingServletRequestPartException
     */
    @ApiResponse(
            responseCode = "400",
            description = "MissingServletRequestPartException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Required part 'file' is not present.",
                              "instance": "/mvc-extended-problem-detail/missing-servlet-request-part-exception"
                            }
                            """)))
    @PutMapping("/missing-servlet-request-part-exception")
    public void missingServletRequestPartException(@Parameter(example = "demo.txt") @RequestPart MultipartFile file) {
        logger.info("missingServletRequestPartException, file: " + file);
    }

    /**
     * @see ServletRequestBindingException
     */
    @ApiResponse(
            responseCode = "400",
            description = "ServletRequestBindingException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "instance": "/mvc-extended-problem-detail/servlet-request-binding-exception"
                            }
                            """)))
    @GetMapping("/servlet-request-binding-exception")
    public void servletRequestBindingException() throws ServletRequestBindingException {
        logger.info("servletRequestBindingException");
        throw new ServletRequestBindingException("binding error");
    }

    /**
     * @see UnsatisfiedServletRequestParameterException
     */
    @Operation(parameters = @Parameter(name = "type", in = ParameterIn.QUERY, example = "1"))
    @ApiResponse(
            responseCode = "400",
            description = "UnsatisfiedServletRequestParameterException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Invalid request parameters.",
                              "instance": "/mvc-extended-problem-detail/unsatisfied-servlet-request-parameter-exception"
                            }
                            """)))
    @GetMapping(path = "/unsatisfied-servlet-request-parameter-exception", params = {"type=1", "exist", "!debug"})
    public void unsatisfiedServletRequestParameterException() {
        logger.info("unsatisfiedServletRequestParameterException");
    }

    /**
     * @see org.springframework.web.bind.MissingRequestValueException
     */
    @ApiResponse(
            responseCode = "400",
            description = "org.springframework.web.bind.MissingRequestValueException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "instance": "/mvc-extended-problem-detail/org-spring-web-bind-missing-request-value-exception"
                            }
                            """)))
    @GetMapping("/org-spring-web-bind-missing-request-value-exception")
    public void orgSpringWebBindMissingRequestValueException() throws org.springframework.web.bind.MissingRequestValueException {
        logger.info("orgSpringWebBindMissingRequestValueException");
        throw new org.springframework.web.bind.MissingRequestValueException("missing request value", true);
    }


    /**
     * @see MissingMatrixVariableException
     */
    @ApiResponse(
            responseCode = "400",
            description = "MissingMatrixVariableException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Required path parameter 'list' is not present.",
                              "instance": "/mvc-extended-problem-detail/missing-matrix-variable-exception/abc;list1=a,b,c"
                            }
                            """)))
    @GetMapping("/missing-matrix-variable-exception/{id}")
    public void missingMatrixVariableException(@Parameter(example = "abc") @PathVariable String id,
                                               @Parameter(example = "a,b,c") @MatrixVariable List<String> list) {
        logger.info("missingMatrixVariableException, id: " + id + ", list: " + list);
    }

    /**
     * @see MissingRequestCookieException
     */
    @ApiResponse(
            responseCode = "400",
            description = "MissingRequestCookieException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Required cookie 'cookieValue' is not present.",
                              "instance": "/mvc-extended-problem-detail/missing-request-cookie-exception"
                            }
                            """)))
    @GetMapping("/missing-request-cookie-exception")
    public void missingRequestCookieException(@Parameter(example = "cookie-value") @CookieValue String cookieValue) {
        logger.info("missingRequestCookieException, cookieValue: " + cookieValue);
    }

    /**
     * @see MissingRequestHeaderException
     */
    @ApiResponse(
            responseCode = "400",
            description = "MissingRequestHeaderException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Required header 'header' is not present.",
                              "instance": "/mvc-extended-problem-detail/missing-request-header-exception"
                            }
                            """)))
    @GetMapping("/missing-request-header-exception")
    public void missingRequestHeaderException(@Parameter(example = "header-value") @RequestHeader String header) {
        logger.info("missingRequestHeaderException, header: " + header);
    }

    /**
     * @see MethodArgumentNotValidException
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(name = "example", value = """
                    {
                      "name": "abc",
                      "password": "123",
                      "address": {
                        "street": "",
                        "geo": {
                          "location": {
                            "code": "LOC-100"
                          }
                        }
                      }
                    }
                    """)))
    @ApiResponse(
            responseCode = "400",
            description = "MethodArgumentNotValidException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Invalid request content.",
                              "instance": "/mvc-extended-problem-detail/method-argument-not-valid-exception",
                              "errors": [
                                {
                                  "type": "REQUEST_BODY",
                                  "target": "name",
                                  "message": "Name length must be between 6-10"
                                },
                                {
                                  "type": "REQUEST_BODY",
                                  "target": "age",
                                  "message": "Age cannot be null"
                                },
                                {
                                  "type": "REQUEST_BODY",
                                  "target": "password",
                                  "message": "Password and confirm password do not match"
                                },
                                {
                                  "type": "REQUEST_BODY",
                                  "target": "confirmPassword",
                                  "message": "Password and confirm password do not match"
                                },
                                {
                                  "type": "REQUEST_BODY",
                                  "target": "address.street",
                                  "message": "Street cannot be blank"
                                }
                              ]
                            }
                            """)))
    @PostMapping("/method-argument-not-valid-exception")
    public void methodArgumentNotValidException(@RequestBody @Validated MvcProblemDetailRequest problemDetailRequest) {
        logger.info("methodArgumentNotValidException, problemDetailRequest: " + problemDetailRequest);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#cookieValue(CookieValue, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException cookieValue",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
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
                            """)))
    @GetMapping("/handler-method-validation-exception-cookie-value")
    public void handlerMethodValidationExceptionCookieValue(@Parameter(example = "a") @CookieValue @Length(min = 2, message = "{mvc.example.request.cookie.name.length}") String name) {
        logger.info("handlerMethodValidationExceptionCookieValue, name: " + name);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#matrixVariable(MatrixVariable, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException matrixVariable",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-matrix-variable/abc;list=a,b,c",
                              "errors": [
                                {
                                  "type": "MATRIX_VARIABLE",
                                  "target": "list",
                                  "message": "Maximum size is 2"
                                }
                              ]
                            }
                            """)))
    @GetMapping("/handler-method-validation-exception-matrix-variable/{id}")
    public void handlerMethodValidationExceptionMatrixVariable(@Parameter(example = "abc") @PathVariable String id,
                                                               @Parameter(example = "a,b,c") @MatrixVariable @Size(max = 2, message = "{mvc.example.request.matrix.list.size}") List<String> list) {
        logger.info("handlerMethodValidationExceptionMatrixVariable, id: " + id + ", list: " + list);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#modelAttribute(ModelAttribute, ParameterErrors)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException modelAttribute",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-model-attribute",
                              "errors": [
                                {
                                  "type": "MODEL_ATTRIBUTE",
                                  "target": "password",
                                  "message": "Password cannot be empty"
                                }
                              ]
                            }
                            """)))
    @GetMapping("/handler-method-validation-exception-model-attribute")
    public void handlerMethodValidationExceptionModelAttribute(@MvcCheckPassword(message = "{mvc.example.request.password.required}") MvcProblemDetailRequest problemDetailRequest) {
        logger.info("handlerMethodValidationExceptionModelAttribute, problemDetailRequest: " + problemDetailRequest);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#pathVariable(PathVariable, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException pathVariable",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-path-variable/a",
                              "errors": [
                                {
                                  "type": "PATH_VARIABLE",
                                  "target": "id",
                                  "message": "ID minimum length is 2"
                                }
                              ]
                            }
                            """)))
    @GetMapping("/handler-method-validation-exception-path-variable/{id}")
    public void handlerMethodValidationExceptionPathVariable(@Parameter(example = "a") @PathVariable @Length(min = 2, message = "{mvc.example.request.id.length}") String id) {
        logger.info("handlerMethodValidationExceptionPathVariable, id: " + id);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBody(RequestBody, ParameterErrors)
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(name = "example", value = """
                    {
                      "name": "abc"
                    }
                    """)))
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException requestBody",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-body",
                              "errors": [
                                {
                                  "type": "REQUEST_BODY",
                                  "target": "password",
                                  "message": "Password cannot be empty"
                                }
                              ]
                            }
                            """)))
    @PostMapping("/handler-method-validation-exception-request-body")
    public void handlerMethodValidationExceptionRequestBody(@RequestBody @MvcCheckPassword(message = "{mvc.example.request.password.required}") MvcProblemDetailRequest problemDetailRequest) {
        logger.info("handlerMethodValidationExceptionRequestBody, problemDetailRequest: " + problemDetailRequest);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBodyValidationResult(RequestBody, ParameterValidationResult)
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(name = "example", value = """
                    [
                      "",
                      "a"
                    ]
                    """)))
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException requestBodyValidationResult",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result",
                              "errors": [
                                {
                                  "type": "REQUEST_BODY",
                                  "message": "Element cannot contain empty values"
                                }
                              ]
                            }
                            """)))
    @PostMapping("/handler-method-validation-exception-request-body-validation-result")
    public void handlerMethodValidationExceptionRequestBodyValidationResult(@RequestBody List<@NotBlank(message = "{mvc.example.request.list.element.blank}") String> list) {
        logger.info("handlerMethodValidationExceptionRequestBodyValidationResult, list: " + list);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestHeader(RequestHeader, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException requestHeader",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
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
                            """)))
    @GetMapping("/handler-method-validation-exception-request-header")
    public void handlerMethodValidationExceptionRequestHeader(@Parameter(example = "a") @RequestHeader @Length(min = 2, message = "{mvc.example.request.header.length}") String headerValue) {
        logger.info("handlerMethodValidationExceptionRequestHeader, headerValue: " + headerValue);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestParam(RequestParam, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException requestParam",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-param",
                              "errors": [
                                {
                                  "type": "QUERY_PARAMETER",
                                  "target": "param",
                                  "message": "Parameter cannot be empty"
                                },
                                {
                                  "type": "QUERY_PARAMETER",
                                  "target": "param2",
                                  "message": "Parameter 2 cannot be null"
                                },
                                {
                                  "type": "QUERY_PARAMETER",
                                  "target": "param2",
                                  "message": "Parameter 2 cannot be blank"
                                }
                              ]
                            }
                            """)))
    @GetMapping("/handler-method-validation-exception-request-param")
    public void handlerMethodValidationExceptionRequestParam(@Parameter @NotBlank(message = "{mvc.example.request.parameter.blank}") String param,
                                                             @Parameter @NotNull(message = "{mvc.example.request.parameter-secondary.missing}") @NotBlank(message = "{mvc.example.request.parameter-secondary.blank}") String param2) {
        logger.info("handlerMethodValidationExceptionRequestParam, param: " + param + ", param2: " + param2);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestPart(RequestPart, ParameterErrors)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException requestPart",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-request-part",
                              "errors": [
                                {
                                  "type": "REQUEST_PART",
                                  "target": "file",
                                  "message": "File cannot be empty"
                                }
                              ]
                            }
                            """)))
    @GetMapping("/handler-method-validation-exception-request-part")
    public void handlerMethodValidationExceptionRequestPart(@Parameter(example = "demo.txt") @RequestPart(required = false) @MvcCheckMultipartFile(extensionIncludeMessage = "{mvc.example.upload.file.unsupported-type}",
            extensionInclude = "txt", requiredMessage = "{mvc.example.upload.file.not-empty}") MultipartFile file) {
        logger.info("handlerMethodValidationExceptionRequestPart, file: " + file);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#other(ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException other",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/mvc-extended-problem-detail/handler-method-validation-exception-other"
                            }
                            """)))
    @GetMapping("/handler-method-validation-exception-other")
    public void handlerMethodValidationExceptionOther(@SessionAttribute(required = false) @NotBlank(message = "{mvc.example.request.session-attribute.blank}")
                                                      String sessionAttribute,
                                                      @RequestAttribute(required = false) @NotBlank(message = "{mvc.example.request.request-attribute.blank}")
                                                      String requestAttribute,
                                                      @Value("") @NotBlank(message = "{mvc.example.request.value.blank}") String value) {
        logger.info("handlerMethodValidationExceptionOther, sessionAttribute: " + sessionAttribute
                + ", requestAttribute: " + requestAttribute + ", value: " + value);
    }

    /**
     * @see AsyncRequestTimeoutException
     */
    @ApiResponse(
            responseCode = "503",
            description = "AsyncRequestTimeoutException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Service Unavailable",
                              "status": 503,
                              "instance": "/mvc-extended-problem-detail/async-request-timeout-exception"
                            }
                            """)))
    @GetMapping("/async-request-timeout-exception")
    public DeferredResult<Void> asyncRequestTimeoutException() {
        logger.info("asyncRequestTimeoutException");
        return new DeferredResult<>(1L);
    }

    /**
     * @see ErrorResponseException
     */
    @ApiResponse(
            responseCode = "400",
            description = "ErrorResponseException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "instance": "/mvc-extended-problem-detail/error-response-exception"
                            }
                            """)))
    @GetMapping("/error-response-exception")
    public void errorResponseException() {
        logger.info("errorResponseException");
        throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
    }

    /**
     * @see PayFailedException
     */
    @ApiResponse(
            responseCode = "500",
            description = "MvcExtendedErrorResponseException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Payment failed",
                              "status": 500,
                              "detail": "The payment request could not be processed.",
                              "instance": "/mvc-extended-problem-detail/extended-error-response-exception",
                              "errors": [
                                {
                                  "type": "BUSINESS",
                                  "message": "Insufficient balance"
                                },
                                {
                                  "type": "BUSINESS",
                                  "message": "Payment is too frequent"
                                }
                              ]
                            }
                            """)))
    @GetMapping("/extended-error-response-exception")
    public void extendedErrorResponseException() {
        logger.info("businessException");
        List<String> errorCodes = List.of(
                "{mvc.example.payment.error.insufficient-balance}",
                "{mvc.example.payment.error.too-frequent}"
        );
        throw new PayFailedException(errorCodes, errorsPropertyName);
    }

    /**
     * @see ResponseStatusException
     */
    @ApiResponse(
            responseCode = "400",
            description = "ResponseStatusException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "exception",
                              "instance": "/mvc-extended-problem-detail/response-status-exception"
                            }
                            """)))
    @GetMapping("/response-status-exception")
    public void responseStatusException() {
        logger.info("responseStatusException");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    /**
     * @see ServerWebInputException
     */
    @ApiResponse(
            responseCode = "400",
            description = "ServerWebInputException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "server web input error",
                              "instance": "/mvc-extended-problem-detail/server-web-input-exception"
                            }
                            """)))
    @GetMapping("/server-web-input-exception")
    public void serverWebInputException() {
        logger.info("serverWebInputException");
        throw new ServerWebInputException("server web input error");
    }

    /**
     * @see UnsatisfiedRequestParameterException
     */
    @Operation(parameters = @Parameter(name = "type", in = ParameterIn.QUERY, example = "1"))
    @ApiResponse(
            responseCode = "400",
            description = "UnsatisfiedRequestParameterException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Invalid request parameters.",
                              "instance": "/mvc-extended-problem-detail/unsatisfied-request-parameter-exception"
                            }
                            """)))
    @GetMapping(path = "/unsatisfied-request-parameter-exception", params = {"type=1", "exist", "!debug"})
    public void unsatisfiedRequestParameterException() {
        logger.info("unsatisfiedRequestParameterException");
    }

    /**
     * @see org.springframework.web.server.MissingRequestValueException
     */
    @ApiResponse(
            responseCode = "400",
            description = "org.springframework.web.server.MissingRequestValueException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Required request param 'id' is not present.",
                              "instance": "/mvc-extended-problem-detail/org-springframework-web-server-missing-request-value-exception"
                            }
                            """)))
    @GetMapping("/org-springframework-web-server-missing-request-value-exception")
    public void orgSpringframeworkWebServerMissingRequestValueException(HttpServletRequest httpServletRequest,
                                                                        String id) throws Exception {
        logger.info("missingRequestValueException, id: " + id);
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(httpServletRequest);
        Optional.ofNullable(handlerExecutionChain)
                .map(HandlerExecutionChain::getHandler)
                .map(handlerMethod -> (HandlerMethod) handlerMethod)
                .map(HandlerMethod::getMethodParameters)
                .map(methodParameters -> methodParameters[0])
                .ifPresent(methodParameter -> {
                    throw new org.springframework.web.server.MissingRequestValueException("id", String.class, "request param", methodParameter);
                });
    }

    /**
     * @see WebExchangeBindException
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "name": "abc",
                              "password": "123",
                              "address": {
                                "street": "",
                                "geo": {
                                  "location": {
                                    "code": "LOC-100"
                                  }
                                }
                              }
                            }
                            """)))
    @ApiResponse(
            responseCode = "400",
            description = "WebExchangeBindException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Invalid request content.",
                              "instance": "/mvc-extended-problem-detail/web-exchange-bind-exception",
                              "errors": [
                                {
                                  "type": "MODEL_ATTRIBUTE",
                                  "target": "name",
                                  "message": "Name length must be between 6-10"
                                },
                                {
                                  "type": "MODEL_ATTRIBUTE",
                                  "target": "age",
                                  "message": "Age cannot be null"
                                },
                                {
                                  "type": "MODEL_ATTRIBUTE",
                                  "target": "password",
                                  "message": "Password and confirm password do not match"
                                },
                                {
                                  "type": "MODEL_ATTRIBUTE",
                                  "target": "confirmPassword",
                                  "message": "Password and confirm password do not match"
                                },
                                {
                                  "type": "MODEL_ATTRIBUTE",
                                  "target": "address.street",
                                  "message": "Street cannot be blank"
                                }
                              ]
                            }
                            """)))
    @PostMapping("/web-exchange-bind-exception")
    public void webExchangeBindException(HttpServletRequest httpServletRequest,
                                         @RequestBody @Validated MvcProblemDetailRequest problemDetailRequest,
                                         BindingResult bindingResult) throws Exception {
        logger.info("webExchangeBindException, problemDetailRequest: " + problemDetailRequest);
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(httpServletRequest);
        Optional.ofNullable(handlerExecutionChain)
                .map(HandlerExecutionChain::getHandler)
                .map(handlerMethod -> (HandlerMethod) handlerMethod)
                .map(HandlerMethod::getMethodParameters)
                .map(methodParameters -> methodParameters[0])
                .ifPresent(methodParameter -> {
                    throw new WebExchangeBindException(methodParameter, bindingResult);
                });
    }

    /**
     * @see MethodNotAllowedException
     */
    @ApiResponse(
            responseCode = "405",
            description = "MethodNotAllowedException",
            headers = @Header(name = "Allow", schema = @Schema(type = "string", example = "GET, POST")),
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Method Not Allowed",
                              "status": 405,
                              "detail": "Supported methods: [GET, POST]",
                              "instance": "/mvc-extended-problem-detail/method-not-allowed-exception"
                            }
                            """)))
    @DeleteMapping("/method-not-allowed-exception")
    public void methodNotAllowedException() {
        List<HttpMethod> supportedMethods = Arrays.asList(HttpMethod.GET, HttpMethod.POST);
        logger.info("methodNotAllowedException");
        throw new MethodNotAllowedException(HttpMethod.DELETE, supportedMethods);
    }

    /**
     * @see NotAcceptableStatusException
     */
    @ApiResponse(
            responseCode = "406",
            description = "NotAcceptableStatusException",
            headers = @Header(name = "Accept", schema = @Schema(type = "string", example = MediaType.APPLICATION_JSON_VALUE)),
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Not Acceptable",
                              "status": 406,
                              "detail": "Acceptable representations: [application/json].",
                              "instance": "/mvc-extended-problem-detail/not-acceptable-status-exception"
                            }
                            """)))
    @GetMapping("/not-acceptable-status-exception")
    public void notAcceptableStatusException() {
        logger.info("notAcceptableStatusException");
        throw new NotAcceptableStatusException(List.of(MediaType.APPLICATION_JSON));
    }

    /**
     * @see UnsupportedMediaTypeStatusException
     */
    @ApiResponse(
            responseCode = "415",
            description = "UnsupportedMediaTypeStatusException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Unsupported Media Type",
                              "status": 415,
                              "detail": "Could not parse Content-Type.",
                              "instance": "/mvc-extended-problem-detail/unsupported-media-type-status-exception"
                            }
                            """)))
    @PostMapping("/unsupported-media-type-status-exception")
    public void unsupportedMediaTypeStatusException() {
        logger.info("unsupportedMediaTypeStatusException");
        throw new UnsupportedMediaTypeStatusException("unsupported media type");
    }

    /**
     * @see ServerErrorException
     */
    @ApiResponse(
            responseCode = "500",
            description = "ServerErrorException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "server error",
                              "instance": "/mvc-extended-problem-detail/server-error-exception"
                            }
                            """)))
    @GetMapping("/server-error-exception")
    public void serverErrorException() {
        logger.info("serverErrorException");
        throw new ServerErrorException("server error", new RuntimeException());
    }

    /**
     * @see PayloadTooLargeException
     */
    @ApiResponse(
            responseCode = "413",
            description = "PayloadTooLargeException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Content Too Large",
                              "status": 413,
                              "detail": "payload too large",
                              "instance": "/mvc-extended-problem-detail/payload-too-large-exception"
                            }
                            """)))
    @PostMapping("/payload-too-large-exception")
    public void payloadTooLargeException(@Parameter(example = "demo.txt") @RequestPart MultipartFile file) {
        logger.info("payloadTooLargeException, file: " + file);
        PayloadTooLargeException payloadTooLarge = new PayloadTooLargeException(new RuntimeException("payload too large"));
        payloadTooLarge.setDetail("payload too large");
        throw payloadTooLarge;
    }

    /**
     * @see MaxUploadSizeExceededException
     */
    @ApiResponse(
            responseCode = "413",
            description = "MaxUploadSizeExceededException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Content Too Large",
                              "status": 413,
                              "detail": "Maximum upload size exceeded",
                              "instance": "/mvc-extended-problem-detail/max-upload-size-exceeded-exception"
                            }
                            """)))
    @PostMapping("/max-upload-size-exceeded-exception")
    public void maxUploadSizeExceededException(@Parameter(example = "oversized.txt") @RequestPart MultipartFile file) {
        logger.info("maxUploadSizeExceededException, file: " + file);
    }

    /**
     * @see ConversionNotSupportedException
     */
    @ApiResponse(
            responseCode = "500",
            description = "ConversionNotSupportedException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Failed to convert 'null' with value: 'test-value'",
                              "instance": "/mvc-extended-problem-detail/conversion-not-supported-exception"
                            }
                            """)))
    @GetMapping("/conversion-not-supported-exception")
    public void conversionNotSupportedException(HttpServletRequest httpServletRequest, @Parameter(example = "test-value") String data) throws Exception {
        logger.info("conversionNotSupportedException, data: " + data);
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(httpServletRequest);
        if (null == handlerExecutionChain) {
            throw new MissingServletRequestParameterException("data", "String");
        }
        HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();
        MvcProblemDetailRequest problemDetailRequest = simpleTypeConverter.convertIfNecessary(data, MvcProblemDetailRequest.class, methodParameters[0]);
        logger.info("conversionNotSupportedException, problemDetailRequest: " + problemDetailRequest);
    }

    /**
     * @see MethodArgumentConversionNotSupportedException
     */
    @ApiResponse(
            responseCode = "500",
            description = "MethodArgumentConversionNotSupportedException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Failed to convert 'error' with value: 'test-value'",
                              "instance": "/mvc-extended-problem-detail/method-argument-conversion-not-supported-exception"
                            }
                            """)))
    @GetMapping("/method-argument-conversion-not-supported-exception")
    public void methodArgumentConversionNotSupportedException(@Parameter(example = "test-value") @RequestParam MvcProblemDetailRequest error) {
        logger.info("methodArgumentConversionNotSupportedException, error: " + error);
    }

    /**
     * @see TypeMismatchException
     */
    @ApiResponse(
            responseCode = "400",
            description = "TypeMismatchException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Failed to convert 'null' with value: 'test'",
                              "instance": "/mvc-extended-problem-detail/type-mismatch-exception"
                            }
                            """)))
    @GetMapping("/type-mismatch-exception")
    public void typeMismatchException() {
        logger.info("typeMismatchException");
        throw new TypeMismatchException("test", Integer.class);
    }

    /**
     * @see MethodArgumentTypeMismatchException
     */
    @ApiResponse(
            responseCode = "400",
            description = "MethodArgumentTypeMismatchException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Failed to convert 'integer' with value: 'a'",
                              "instance": "/mvc-extended-problem-detail/method-argument-type-mismatch-exception"
                            }
                            """)))
    @GetMapping("/method-argument-type-mismatch-exception")
    public void methodArgumentTypeMismatchException(@Parameter(example = "a") Integer integer) {
        logger.info("methodArgumentTypeMismatchException, integer: " + integer);
    }

    /**
     * @see HttpMessageNotReadableException
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(name = "example", value = """
                    {"name":
                    """)))
    @ApiResponse(
            responseCode = "400",
            description = "HttpMessageNotReadableException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Failed to read request",
                              "instance": "/mvc-extended-problem-detail/http-message-not-readable-exception"
                            }
                            """)))
    @PostMapping("/http-message-not-readable-exception")
    public void httpMessageNotReadableException(@RequestBody MvcProblemDetailRequest data) {
        logger.info("httpMessageNotReadableException, data: " + data);
    }

    /**
     * @see HttpMessageNotWritableException
     * @see MvcProblemDetailResponseSerializer
     */
    @ApiResponse(
            responseCode = "500",
            description = "HttpMessageNotWritableException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Failed to write request",
                              "instance": "/mvc-extended-problem-detail/http-message-not-writable-exception"
                            }
                            """)))
    @GetMapping("/http-message-not-writable-exception")
    public MvcProblemDetailResponse httpMessageNotWritableException() {
        logger.info("httpMessageNotWritableException");
        return new MvcProblemDetailResponse();
    }

    /**
     * @see MethodValidationException
     * @see MvcMethodValidationConfiguration#validationPostProcessor()
     */
    @ApiResponse(
            responseCode = "500",
            description = "MethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Validation failed",
                              "instance": "/mvc-extended-problem-detail/method-validation-exception"
                            }
                            """)))
    @GetMapping("/method-validation-exception")
    public void methodValidationException() {
        logger.info("methodValidationException");
        MvcProblemDetailRequest problemDetailRequest = new MvcProblemDetailRequest();
        problemDetailRequest.setPassword("a");
        problemDetailRequest.setName("");
        problemDetailService.createProblemDetail(null, problemDetailRequest);
    }

    /**
     * @see AsyncRequestNotUsableException
     */
    @ApiResponse(responseCode = "200", description = "AsyncRequestNotUsableException",
            content = @Content(mediaType = "text/event-stream",
                    examples = @ExampleObject(name = "example",
                            value = "data:event 0\n\ndata:event 1\n\ndata:event 2\n")))
    @GetMapping("/async-request-not-usable-exception")
    public SseEmitter asyncRequestNotUsableException() {
        logger.info("asyncRequestNotUsableException");
        SseEmitter emitter = new SseEmitter(1000L);
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(100);
                    logger.info("asyncRequestNotUsableException, emitter send: " + i);
                    emitter.send("event " + i);
                }
                emitter.complete();
            } catch (InterruptedException e) {
                logger.error("Thread sleep interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("Emitter error: " + e.getClass().getSimpleName());
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

}
