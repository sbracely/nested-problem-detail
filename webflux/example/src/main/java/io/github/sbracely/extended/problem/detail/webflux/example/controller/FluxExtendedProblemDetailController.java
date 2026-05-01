package io.github.sbracely.extended.problem.detail.webflux.example.controller;

import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import io.github.sbracely.extended.problem.detail.webflux.example.config.FluxMethodValidationConfiguration;
import io.github.sbracely.extended.problem.detail.webflux.example.exception.PayFailedException;
import io.github.sbracely.extended.problem.detail.webflux.example.request.FluxProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.service.FluxProblemDetailService;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.FluxCheckFilePart;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.FluxCheckPassword;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/flux-extended-problem-detail")
@Tag(name = "WebFlux Example", description = "Endpoints that intentionally trigger different WebFlux and validation exceptions.")
public class FluxExtendedProblemDetailController {

    private static final Logger logger = LoggerFactory.getLogger(FluxExtendedProblemDetailController.class);

    private final FluxProblemDetailService problemDetailService;

    public FluxExtendedProblemDetailController(FluxProblemDetailService problemDetailService) {
        this.problemDetailService = problemDetailService;
    }

    /**
     * @see MethodNotAllowedException
     */
    @ApiResponse(
            responseCode = "405",
            description = "MethodNotAllowedException",
            headers = @Header(name = "Allow", schema = @Schema(type = "string", example = "GET")),
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Method Not Allowed",
                              "status": 405,
                              "detail": "Supported methods: [GET]",
                              "instance": "/flux-extended-problem-detail/method-not-allowed-exception"
                            }
                            """)))
    @GetMapping("/method-not-allowed-exception")
    public Mono<Void> methodNotAllowedException() {
        logger.info("methodNotAllowedException");
        return Mono.empty();
    }

    /**
     * @see NotAcceptableStatusException
     */
    @Operation(parameters = @Parameter(name = "Accept", in = ParameterIn.HEADER, example = "application/xml"))
    @ApiResponse(
            responseCode = "406",
            description = "NotAcceptableStatusException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Not Acceptable",
                              "status": 406,
                              "detail": "Acceptable representations: [application/json].",
                              "instance": "/flux-extended-problem-detail/not-acceptable-status-exception"
                            }
                            """)))
    @GetMapping(path = "/not-acceptable-status-exception", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> notAcceptableStatusException() {
        logger.info("notAcceptableStatusException");
        return Mono.empty();
    }

    /**
     * @see UnsupportedMediaTypeStatusException
     */
    @ApiResponse(
            responseCode = "415",
            description = "UnsupportedMediaTypeStatusException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Unsupported Media Type",
                              "status": 415,
                              "instance": "/flux-extended-problem-detail/unsupported-media-type-status-exception"
                            }
                            """)))
    @PostMapping(path = "/unsupported-media-type-status-exception", consumes = MediaType.APPLICATION_XML_VALUE)
    public Mono<Void> unsupportedMediaTypeStatusException() {
        logger.info("unsupportedMediaTypeStatusException");
        return Mono.empty();
    }

    /**
     * @see MissingRequestValueException
     */
    @ApiResponse(
            responseCode = "400",
            description = "MissingRequestValueException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", summary = "Validation error", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Required query parameter 'id' is not present.",
                              "instance": "/flux-extended-problem-detail/missing-request-value-exception"
                            }
                            """)))
    @GetMapping("/missing-request-value-exception")
    public Mono<Void> missingRequestValueException(@Parameter(example = "1") @RequestParam String id) {
        logger.info("missingRequestValueException, id: {}", id);
        return Mono.empty();
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
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Invalid request parameters.",
                              "instance": "/flux-extended-problem-detail/unsatisfied-request-parameter-exception"
                            }
                            """)))
    @GetMapping(path = "/unsatisfied-request-parameter-exception", params = {"type=1", "exist", "!debug"})
    public Mono<Void> unsatisfiedRequestParameterException() {
        logger.info("unsatisfiedRequestParameterException");
        return Mono.empty();
    }

    /**
     * @see WebExchangeBindException
     */
    @ApiResponse(
            responseCode = "400",
            description = "WebExchangeBindException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @PostMapping("/web-exchange-bind-exception")
    public Mono<Void> webExchangeBindException(@RequestBody @Validated FluxProblemDetailRequest problemDetailRequest) {
        logger.info("webExchangeBindException, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#cookieValue(CookieValue, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @GetMapping("/handler-method-validation-exception-cookie-value")
    public Mono<Void> handlerMethodValidationExceptionCookieValue(@CookieValue @NotBlank(message = "{flux.example.request.cookie.blank}") String cookieValue) {
        logger.info("handlerMethodValidationExceptionCookieValue, cookieValue: {}", cookieValue);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#matrixVariable(MatrixVariable, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @GetMapping("/handler-method-validation-exception-matrix/{id}")
    public Mono<Void> handlerMethodValidationExceptionMatrixVariable(@Parameter(example = "abc") @PathVariable String id,
                                                                      @MatrixVariable @Size(max = 2, message = "{flux.example.request.matrix.list.size}") List<String> list) {
        logger.info("handlerMethodValidationExceptionMatrixVariable, id: {}, list: {}", id, list);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#modelAttribute(ModelAttribute, ParameterErrors)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @GetMapping("/handler-method-validation-exception-model-attribute")
    public Mono<Void> handlerMethodValidationExceptionModelAttribute(@FluxCheckPassword(message = "{flux.example.request.password.required}") FluxProblemDetailRequest problemDetailRequest) {
        logger.info("handlerMethodValidationExceptionModelAttribute, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#pathVariable(PathVariable, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @GetMapping("/handler-method-validation-exception-path-variable/{id}")
    public Mono<Void> handlerMethodValidationExceptionPathVariable(@Parameter(example = "abc") @PathVariable @Size(min = 5, message = "{flux.example.request.id.length}") String id) {
        logger.info("handlerMethodValidationExceptionPathVariable, id: {}", id);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBody(RequestBody, ParameterErrors)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @PostMapping("/handler-method-validation-exception-request-body")
    public Mono<Void> handlerMethodValidationExceptionRequestBody(@RequestBody @FluxCheckPassword(message = "{flux.example.request.password.required}") FluxProblemDetailRequest problemDetailRequest) {
        logger.info("handlerMethodValidationExceptionRequestBody, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBodyValidationResult(RequestBody, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @PostMapping("/handler-method-validation-exception-request-body-validation-result")
    public Mono<Void> handlerMethodValidationExceptionRequestBodyValidationResult(@RequestBody List<@NotBlank(message = "{flux.example.request.list.element.blank}") String> list) {
        logger.info("handlerMethodValidationExceptionRequestBodyValidationResult, list: {}", list);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestHeader(RequestHeader, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @GetMapping(path = "/handler-method-validation-exception-request-header")
    public Mono<Void> handlerMethodValidationExceptionRequestHeader(@RequestHeader @NotBlank(message = "{flux.example.request.header.blank}") String headerValue) {
        logger.info("handlerMethodValidationExceptionRequestHeader, headerValue: {}", headerValue);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestParam(RequestParam, ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @GetMapping("/handler-method-validation-exception-request-param")
    public Mono<Void> handlerMethodValidationExceptionRequestParam(@RequestParam @NotBlank(message = "{flux.example.request.parameter.blank}") String param,
                                                                    @RequestParam @Size(min = 5, message = "{flux.example.request.value.length}") String value) {
        logger.info("handlerMethodValidationExceptionRequestParam, param: {}, value: {}", param, value);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestPart(RequestPart, ParameterErrors)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @PostMapping("/handler-method-validation-exception-request-part")
    public Mono<Void> handlerMethodValidationExceptionRequestPart(@RequestPart(required = false)
                                                                   @FluxCheckFilePart(requiredMessage = "{flux.example.upload.file.not-empty}") FilePart filePart) {
        logger.info("handlerMethodValidationExceptionRequestPart, filePart: {}", filePart);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#other(ParameterValidationResult)
     */
    @ApiResponse(
            responseCode = "400",
            description = "HandlerMethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Validation failure",
                              "instance": "/flux-extended-problem-detail/handler-method-validation-exception-other"
                            }
                            """)))
    @GetMapping("/handler-method-validation-exception-other")
    public Mono<Void> handlerMethodValidationExceptionOther(
            @SessionAttribute(required = false) @NotBlank(message = "{flux.example.request.session-attribute.blank}") String sessionAttribute,
            @RequestAttribute(required = false) @NotBlank(message = "{flux.example.request.request-attribute.blank}") String requestAttribute,
            @Value("") @NotBlank(message = "{flux.example.request.value.blank}") String value) {
        logger.info("handlerMethodValidationExceptionOther, sessionAttribute: {}, requestAttribute: {}, value: {}", sessionAttribute, requestAttribute, value);
        return Mono.empty();
    }

    /**
     * @see ServerWebInputException
     */
    @ApiResponse(
            responseCode = "400",
            description = "ServerWebInputException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "server web input error",
                              "instance": "/flux-extended-problem-detail/server-web-input-exception"
                            }
                            """)))
    @GetMapping("/server-web-input-exception")
    public Mono<Void> serverWebInputException() {
        logger.info("serverWebInputException");
        throw new ServerWebInputException("server web input error");
    }

    /**
     * @see ServerErrorException
     */
    @ApiResponse(
            responseCode = "500",
            description = "ServerErrorException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "server error",
                              "instance": "/flux-extended-problem-detail/server-error-exception"
                            }
                            """)))
    @GetMapping("/server-error-exception")
    public Mono<Void> serverErrorException() {
        logger.info("serverErrorException");
        throw new ServerErrorException("server error", new RuntimeException());
    }

    /**
     * @see ResponseStatusException
     */
    @ApiResponse(
            responseCode = "400",
            description = "ResponseStatusException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "exception",
                              "instance": "/flux-extended-problem-detail/response-status-exception"
                            }
                            """)))
    @GetMapping("/response-status-exception")
    public Mono<Void> responseStatusException() {
        logger.info("responseStatusException");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    /**
     * @see ContentTooLargeException
     */
    @ApiResponse(
            responseCode = "413",
            description = "ContentTooLargeException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Payload Too Large",
                              "status": 413,
                              "instance": "/flux-extended-problem-detail/content-too-large-exception"
                            }
                            """)))
    @PostMapping("/content-too-large-exception")
    public Mono<Void> contentTooLargeException(@RequestBody byte[] body) {
        logger.info("contentTooLargeException, body.length: {}", body.length);
        return Mono.empty();
    }

    /**
     * @see PayloadTooLargeException
     */
    @ApiResponse(
            responseCode = "413",
            description = "PayloadTooLargeException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Payload Too Large",
                              "status": 413,
                              "instance": "/flux-extended-problem-detail/payload-too-large-exception"
                            }
                            """)))
    @PostMapping("/payload-too-large-exception")
    public Mono<Void> payloadTooLargeException(@RequestBody byte[] body) {
        logger.info("payloadTooLargeException, body.length: {}", body.length);
        throw new PayloadTooLargeException(new RuntimeException("payload too large"));
    }

    /**
     * @see ErrorResponseException
     */
    @ApiResponse(
            responseCode = "400",
            description = "ErrorResponseException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
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
                            """)))
    @GetMapping("/error-response-exception")
    public Mono<Void> errorResponseException() {
        logger.info("errorResponseException");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setDetail("Error details");
        extendedProblemDetail.setTitle("Error title");
        extendedProblemDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        extendedProblemDetail.setErrors(Arrays.asList(
                new Error(Error.Type.BUSINESS, null, "Error message 1"),
                new Error(Error.Type.BUSINESS, null, "Error message 2")));
        throw new ErrorResponseException(HttpStatus.BAD_REQUEST, extendedProblemDetail, new RuntimeException("business exception"));
    }

    /**
     * @see PayFailedException
     */
    @ApiResponse(
            responseCode = "500",
            description = "PayFailedException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Payment failed",
                              "status": 500,
                              "detail": "The payment request could not be processed.",
                              "instance": "/flux-extended-problem-detail/extended-error-response-exception",
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
    public Mono<Void> extendedErrorResponseException() {
        logger.info("extendedErrorResponseException");
        List<String> errorCodes = Arrays.asList(
                "{flux.example.payment.error.insufficient-balance}",
                "{flux.example.payment.error.too-frequent}"
        );
        throw new PayFailedException(errorCodes);
    }

    /**
     * @see MethodValidationException
     * @see FluxMethodValidationConfiguration#validationPostProcessor()
     */
    @ApiResponse(
            responseCode = "500",
            description = "MethodValidationException",
            content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ExtendedProblemDetail"),
                    examples = @ExampleObject(name = "example", value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Validation failed",
                              "instance": "/flux-extended-problem-detail/method-validation-exception"
                            }
                            """)))
    @GetMapping("/method-validation-exception")
    public Mono<Void> methodValidationException() {
        logger.info("methodValidationException");
        FluxProblemDetailRequest problemDetailRequest = new FluxProblemDetailRequest();
        problemDetailRequest.setPassword("a");
        problemDetailRequest.setName("");
        problemDetailService.createProblemDetail(null, problemDetailRequest);
        return Mono.empty();
    }
}
