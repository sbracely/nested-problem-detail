package io.github.sbracely.extended.problem.detail.webflux.example.controller;

import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import io.github.sbracely.extended.problem.detail.webflux.example.config.MethodValidationConfiguration;
import io.github.sbracely.extended.problem.detail.webflux.example.exception.ExtendedErrorResponseException;
import io.github.sbracely.extended.problem.detail.webflux.example.request.ProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.service.ProblemDetailService;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.CheckFilePart;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.CheckPassword;
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
import org.springframework.web.accept.InvalidApiVersionException;
import org.springframework.web.accept.MissingApiVersionException;
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

    private final ProblemDetailService problemDetailService;

    public FluxExtendedProblemDetailController(ProblemDetailService problemDetailService) {
        this.problemDetailService = problemDetailService;
    }

    /**
     * @see MethodNotAllowedException
     */
    @GetMapping("/method-not-allowed-exception")
    public Mono<Void> methodNotAllowedException() {
        logger.info("methodNotAllowedException");
        return Mono.empty();
    }

    /**
     * @see NotAcceptableStatusException
     */
    @GetMapping(path = "/not-acceptable-status-exception", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> notAcceptableStatusException() {
        logger.info("notAcceptableStatusException");
        return Mono.empty();
    }

    /**
     * @see UnsupportedMediaTypeStatusException
     */
    @PostMapping(path = "/unsupported-media-type-status-exception", consumes = MediaType.APPLICATION_XML_VALUE)
    public Mono<Void> unsupportedMediaTypeStatusException() {
        logger.info("unsupportedMediaTypeStatusException");
        return Mono.empty();
    }

    /**
     * @see MissingRequestValueException
     */
    @GetMapping("/missing-request-value-exception")
    public Mono<Void> missingRequestValueException(@RequestParam String id) {
        logger.info("missingRequestValueException, id: {}", id);
        return Mono.empty();
    }

    /**
     * @see UnsatisfiedRequestParameterException
     */
    @GetMapping(path = "/unsatisfied-request-parameter-exception", params = {"type=1", "exist", "!debug"})
    public Mono<Void> unsatisfiedRequestParameterException() {
        logger.info("unsatisfiedRequestParameterException");
        return Mono.empty();
    }

    /**
     * @see WebExchangeBindException
     */
    @PostMapping("/web-exchange-bind-exception")
    public Mono<Void> webExchangeBindException(@RequestBody @Validated ProblemDetailRequest problemDetailRequest) {
        logger.info("webExchangeBindException, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#cookieValue(CookieValue, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-cookie-value")
    public Mono<Void> handlerMethodValidationExceptionCookieValue(@CookieValue @NotBlank(message = "cookie cannot be empty") String cookieValue) {
        logger.info("handlerMethodValidationExceptionCookieValue, cookieValue: {}", cookieValue);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     */
    @GetMapping("/handler-method-validation-exception-matrix/{id}")
    public Mono<Void> handlerMethodValidationExceptionMatrix(@PathVariable String id,
                                                             @MatrixVariable @Size(max = 2, message = "list maximum size is 2") List<String> list) {
        logger.info("handlerMethodValidationExceptionMatrix, id: {}, list: {}", id, list);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     */
    @GetMapping("/handler-method-validation-exception-model-attribute")
    public Mono<Void> handlerMethodValidationExceptionModelAttribute(@CheckPassword(message = "Password cannot be empty") ProblemDetailRequest problemDetailRequest) {
        logger.info("handlerMethodValidationExceptionModelAttribute, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#pathVariable(PathVariable, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-path-variable/{id}")
    public Mono<Void> handlerMethodValidationExceptionPathVariable(@PathVariable @Size(min = 5, message = "id length must be at least 5") String id) {
        logger.info("handlerMethodValidationExceptionPathVariable, id: {}", id);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBody(RequestBody, ParameterErrors)
     */
    @PostMapping("/handler-method-validation-exception-request-body")
    public Mono<Void> handlerMethodValidationExceptionRequestBody(@RequestBody @CheckPassword(message = "Password cannot be empty") ProblemDetailRequest problemDetailRequest) {
        logger.info("handlerMethodValidationExceptionRequestBody, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBodyValidationResult(RequestBody, ParameterValidationResult)
     */
    @PostMapping("/handler-method-validation-exception-request-body-validation-result")
    public Mono<Void> handlerMethodValidationExceptionRequestBodyValidationResult(@RequestBody List<@NotBlank(message = "Element cannot contain empty values") String> list) {
        logger.info("handlerMethodValidationExceptionRequestBodyValidationResult, list: {}", list);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestHeader(RequestHeader, ParameterValidationResult)
     */
    @GetMapping(path = "/handler-method-validation-exception-request-header")
    public Mono<Void> handlerMethodValidationExceptionRequestHeader(@RequestHeader @NotBlank(message = "Header cannot be empty") String headerValue) {
        logger.info("handlerMethodValidationExceptionRequestHeader, headerValue: {}", headerValue);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestParam(RequestParam, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-request-param")
    public Mono<Void> handlerMethodValidationExceptionRequestParam(@RequestParam @NotBlank(message = "Parameter cannot be empty") String param,
                                                                   @RequestParam @Size(min = 5, message = "Length must be at least 5") String value) {
        logger.info("handlerMethodValidationExceptionRequestParam, param: {}, value: {}", param, value);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestPart(RequestPart, ParameterErrors)
     */
    @PostMapping("/handler-method-validation-exception-request-part")
    public Mono<Void> handlerMethodValidationExceptionRequestPart(@RequestPart(required = false)
                                                                  @CheckFilePart(requiredMessage = "File cannot be empty") FilePart filePart) {
        logger.info("handlerMethodValidationExceptionRequestPart, filePart: {}", filePart);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#other(ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-other")
    public Mono<Void> handlerMethodValidationExceptionOther(
            @SessionAttribute(required = false) @NotBlank(message = "sessionAttribute cannot be empty") String sessionAttribute,
            @RequestAttribute(required = false) @NotBlank(message = "requestAttribute cannot be empty") String requestAttribute,
            @Value("") @NotBlank(message = "value cannot be empty") String value) {
        logger.info("handlerMethodValidationExceptionOther, sessionAttribute: {}, requestAttribute: {}, value: {}", sessionAttribute, requestAttribute, value);
        return Mono.empty();
    }

    /**
     * @see ServerWebInputException
     */
    @GetMapping("/server-web-input-exception")
    public Mono<Void> serverWebInputException() {
        logger.info("serverWebInputException");
        throw new ServerWebInputException("server web input error");
    }

    /**
     * @see ServerErrorException
     */
    @GetMapping("/server-error-exception")
    public Mono<Void> serverErrorException() {
        logger.info("serverErrorException");
        throw new ServerErrorException("server error", new RuntimeException());
    }

    /**
     * @see ResponseStatusException
     */
    @GetMapping("/response-status-exception")
    public Mono<Void> responseStatusException() {
        logger.info("responseStatusException");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    /**
     * @see ContentTooLargeException
     */
    @PostMapping("/content-too-large-exception")
    public Mono<Void> contentTooLargeException(@RequestBody byte[] body) {
        logger.info("contentTooLargeException, body.length: {}", body.length);
        return Mono.empty();
    }

    /**
     * @see InvalidApiVersionException
     */
    @GetMapping("/invalid-api-version-exception")
    public Mono<Void> invalidApiVersionException() {
        logger.info("invalidApiVersionException");
        return Mono.empty();
    }

    /**
     * @see MissingApiVersionException
     */
    @GetMapping("/missing-api-version-exception")
    public Mono<Void> missingApiVersionException() {
        logger.info("missingApiVersionException");
        return Mono.empty();
    }

    /**
     * @see PayloadTooLargeException
     */
    @PostMapping("/payload-too-large-exception")
    public Mono<Void> payloadTooLargeException(@RequestBody byte[] body) {
        logger.info("payloadTooLargeException, body.length: {}", body.length);
        throw new PayloadTooLargeException(new RuntimeException("payload too large"));
    }

    /**
     * @see ErrorResponseException
     */
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
     * @see ExtendedErrorResponseException
     */
    @GetMapping("/extended-error-response-exception")
    public Mono<Void> extendedErrorResponseException() {
        logger.info("extendedErrorResponseException");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setTitle("Payment failed title");
        extendedProblemDetail.setDetail("Payment failed details");
        extendedProblemDetail.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        extendedProblemDetail.setErrors(Arrays.asList(
                new Error(Error.Type.BUSINESS, null, "Insufficient balance"),
                new Error(Error.Type.BUSINESS, null, "Payment frequent")));
        throw new ExtendedErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, extendedProblemDetail);
    }

    /**
     * @see MethodValidationException
     * @see MethodValidationConfiguration#validationPostProcessor()
     */
    @GetMapping("/method-validation-exception")
    public Mono<Void> methodValidationException() {
        logger.info("methodValidationException");
        ProblemDetailRequest problemDetailRequest = new ProblemDetailRequest();
        problemDetailRequest.setPassword("a");
        problemDetailRequest.setName("");
        problemDetailService.createProblemDetail(null, problemDetailRequest);
        return Mono.empty();
    }
}
