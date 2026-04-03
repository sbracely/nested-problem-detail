package com.github.sbracely.extended.problem.detail.test.flux.controller;

import com.github.sbracely.extended.problem.detail.core.Error;
import com.github.sbracely.extended.problem.detail.core.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.flux.config.MethodValidationConfiguration;
import com.github.sbracely.extended.problem.detail.test.flux.exception.ExtendedErrorResponseException;
import com.github.sbracely.extended.problem.detail.test.flux.request.ProblemDetailRequest;
import com.github.sbracely.extended.problem.detail.test.flux.request.valid.annocation.CheckFilePart;
import com.github.sbracely.extended.problem.detail.test.flux.request.valid.annocation.CheckPassword;
import com.github.sbracely.extended.problem.detail.test.flux.service.ProblemDetailService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/flux-extended-problem-detail")
public class FluxExtendedProblemDetailController {

    private final ProblemDetailService problemDetailService;

    public FluxExtendedProblemDetailController(ProblemDetailService problemDetailService) {
        this.problemDetailService = problemDetailService;
    }

    /**
     * @see MethodNotAllowedException
     */
    @GetMapping("/method-not-allowed-exception")
    public Mono<Void> methodNotAllowedException() {
        log.info("methodNotAllowedException");
        return Mono.empty();
    }

    /**
     * @see NotAcceptableStatusException
     */
    @GetMapping(path = "/not-acceptable-status-exception", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> notAcceptableStatusException() {
        log.info("notAcceptableStatusException");
        return Mono.empty();
    }

    /**
     * @see UnsupportedMediaTypeStatusException
     */
    @PostMapping(path = "/unsupported-media-type-status-exception", consumes = MediaType.APPLICATION_XML_VALUE)
    public Mono<Void> unsupportedMediaTypeStatusException() {
        log.info("unsupportedMediaTypeStatusException");
        return Mono.empty();
    }

    /**
     * @see MissingRequestValueException
     */
    @GetMapping("/missing-request-value-exception")
    public Mono<Void> missingRequestValueException(@RequestParam String id) {
        log.info("missingRequestValueException, id: {}", id);
        return Mono.empty();
    }

    /**
     * @see UnsatisfiedRequestParameterException
     */
    @GetMapping(path = "/unsatisfied-request-parameter-exception", params = {"type=1", "exist", "!debug"})
    public Mono<Void> unsatisfiedRequestParameterException() {
        log.info("unsatisfiedRequestParameterException");
        return Mono.empty();
    }

    /**
     * @see WebExchangeBindException
     */
    @PostMapping("/web-exchange-bind-exception")
    public Mono<Void> webExchangeBindException(@RequestBody @Validated ProblemDetailRequest problemDetailRequest) {
        log.info("webExchangeBindException, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#cookieValue(CookieValue, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-cookie-value")
    public Mono<Void> handlerMethodValidationExceptionCookieValue(@CookieValue @NotBlank(message = "cookie cannot be empty") String cookieValue) {
        log.info("handlerMethodValidationExceptionCookieValue, cookieValue: {}", cookieValue);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     */
    @GetMapping("/handler-method-validation-exception-matrix/{id}")
    public Mono<Void> handlerMethodValidationExceptionMatrix(@PathVariable String id,
                                                             @MatrixVariable @Size(max = 2, message = "list maximum size is 2") List<String> list) {
        log.info("handlerMethodValidationExceptionMatrix, id: {}, list: {}", id, list);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     */
    @GetMapping("/handler-method-validation-exception-model-attribute")
    public Mono<Void> handlerMethodValidationExceptionModelAttribute(@CheckPassword(message = "Password cannot be empty") ProblemDetailRequest problemDetailRequest) {
        log.info("handlerMethodValidationExceptionModelAttribute, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#pathVariable(PathVariable, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-path-variable/{id}")
    public Mono<Void> handlerMethodValidationExceptionPathVariable(@PathVariable @Size(min = 5, message = "id length must be at least 5") String id) {
        log.info("handlerMethodValidationExceptionPathVariable, id: {}", id);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBody(RequestBody, ParameterErrors)
     */
    @PostMapping("/handler-method-validation-exception-request-body")
    public Mono<Void> handlerMethodValidationExceptionRequestBody(@RequestBody @CheckPassword(message = "Password cannot be empty") ProblemDetailRequest problemDetailRequest) {
        log.info("handlerMethodValidationExceptionRequestBody, problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBodyValidationResult(RequestBody, ParameterValidationResult)
     */
    @PostMapping("/handler-method-validation-exception-request-body-validation-result")
    public Mono<Void> handlerMethodValidationExceptionRequestBodyValidationResult(@RequestBody List<@NotBlank(message = "Element cannot contain empty values") String> list) {
        log.info("handlerMethodValidationExceptionRequestBodyValidationResult, list: {}", list);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestHeader(RequestHeader, ParameterValidationResult)
     */
    @GetMapping(path = "/handler-method-validation-exception-request-header")
    public Mono<Void> handlerMethodValidationExceptionRequestHeader(@RequestHeader @NotBlank(message = "Header cannot be empty") String headerValue) {
        log.info("handlerMethodValidationExceptionRequestHeader, headerValue: {}", headerValue);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestParam(RequestParam, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-request-param")
    public Mono<Void> handlerMethodValidationExceptionRequestParam(@RequestParam @NotBlank(message = "Parameter cannot be empty") String param,
                                                                   @RequestParam @Size(min = 5, message = "Length must be at least 5") String value) {
        log.info("handlerMethodValidationExceptionRequestParam, param: {}, value: {}", param, value);
        return Mono.empty();
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestPart(RequestPart, ParameterErrors)
     */
    @PostMapping("/handler-method-validation-exception-request-part")
    public Mono<Void> handlerMethodValidationExceptionRequestPart(@RequestPart(required = false)
                                                                  @CheckFilePart(requiredMessage = "File cannot be empty") FilePart filePart) {
        log.info("handlerMethodValidationExceptionRequestPart, filePart: {}", filePart);
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
        log.info("handlerMethodValidationExceptionOther, sessionAttribute: {}, requestAttribute: {}, value: {}", sessionAttribute, requestAttribute, value);
        return Mono.empty();
    }

    /**
     * @see ServerWebInputException
     */
    @GetMapping("/server-web-input-exception")
    public Mono<Void> serverWebInputException() {
        log.info("serverWebInputException");
        throw new ServerWebInputException("server web input error");
    }

    /**
     * @see ServerErrorException
     */
    @GetMapping("/server-error-exception")
    public Mono<Void> serverErrorException() {
        log.info("serverErrorException");
        throw new ServerErrorException("server error", new RuntimeException());
    }

    /**
     * @see ResponseStatusException
     */
    @GetMapping("/response-status-exception")
    public Mono<Void> responseStatusException() {
        log.info("responseStatusException");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    /**
     * @see ContentTooLargeException
     */
    @PostMapping("/content-too-large-exception")
    public Mono<Void> contentTooLargeException(@RequestBody byte[] body) {
        log.info("contentTooLargeException, body.length: {}", body.length);
        return Mono.empty();
    }

    /**
     * @see InvalidApiVersionException
     */
    @GetMapping("/invalid-api-version-exception")
    public Mono<Void> invalidApiVersionException() {
        log.info("invalidApiVersionException");
        return Mono.empty();
    }

    /**
     * @see MissingApiVersionException
     */
    @GetMapping("/missing-api-version-exception")
    public Mono<Void> missingApiVersionException() {
        log.info("missingApiVersionException");
        return Mono.empty();
    }

    /**
     * @see PayloadTooLargeException
     */
    @PostMapping("/payload-too-large-exception")
    public Mono<Void> payloadTooLargeException(@RequestBody byte[] body) {
        log.info("payloadTooLargeException, body.length: {}", body.length);
        throw new PayloadTooLargeException(new RuntimeException("payload too large"));
    }

    /**
     * @see ErrorResponseException
     */
    @GetMapping("/error-response-exception")
    public Mono<Void> errorResponseException() {
        log.info("errorResponseException");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setDetail("Error details");
        extendedProblemDetail.setTitle("Error title");
        extendedProblemDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        extendedProblemDetail.setErrors(Lists.newArrayList(
                new Error(Error.Type.BUSINESS, null, "Error message 1"),
                new Error(Error.Type.BUSINESS, null, "Error message 2")));
        throw new ErrorResponseException(HttpStatus.BAD_REQUEST, extendedProblemDetail, new RuntimeException("business exception"));
    }

    /**
     * @see ExtendedErrorResponseException
     */
    @GetMapping("/extended-error-response-exception")
    public Mono<Void> extendedErrorResponseException() {
        log.info("extendedErrorResponseException");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setTitle("Payment failed title");
        extendedProblemDetail.setDetail("Payment failed details");
        extendedProblemDetail.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        extendedProblemDetail.setErrors(Lists.newArrayList(
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
        log.info("methodValidationException");
        ProblemDetailRequest problemDetailRequest = new ProblemDetailRequest();
        problemDetailRequest.setPassword("a");
        problemDetailRequest.setName("");
        problemDetailService.createProblemDetail(null, problemDetailRequest);
        return Mono.empty();
    }
}
