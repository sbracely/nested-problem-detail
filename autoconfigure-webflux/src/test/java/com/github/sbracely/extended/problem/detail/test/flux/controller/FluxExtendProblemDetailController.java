package com.github.sbracely.extended.problem.detail.test.flux.controller;

import com.github.sbracely.extended.problem.detail.response.Error;
import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.flux.exception.BusinessException;
import com.github.sbracely.extended.problem.detail.test.flux.reuqest.ProblemDetailRequest;
import com.github.sbracely.extended.problem.detail.test.flux.reuqest.valid.annocation.CheckFilePart;
import com.github.sbracely.extended.problem.detail.test.flux.reuqest.valid.annocation.CheckPassword;
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
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.PayloadTooLargeException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/flux-extend-problem-detail")
public class FluxExtendProblemDetailController {

    private final ProblemDetailService problemDetailService;

    public FluxExtendProblemDetailController(ProblemDetailService problemDetailService) {
        this.problemDetailService = problemDetailService;
    }

    @GetMapping("/method-not-allowed")
    public Mono<Void> methodNotAllowed() {
        log.info("method-not-allowed");
        return Mono.empty();
    }

    @GetMapping(path = "/not-acceptable-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> notAcceptableStatus() {
        log.info("not-acceptable-status");
        return Mono.empty();
    }

    @PostMapping(path = "/unsupported-media-type", consumes = MediaType.APPLICATION_XML_VALUE)
    public Mono<Void> unsupportedMediaType() {
        log.info("unsupported media type");
        return Mono.empty();
    }

    @GetMapping("/missing-request-value")
    public Mono<Void> missingRequestValue(@RequestParam String id) {
        log.info("missing-request-value id: {}", id);
        return Mono.empty();
    }

    @GetMapping(path = "/unsatisfied-request-param", params = {"type=1", "exist", "!debug"})
    public Mono<Void> unsatisfiedRequestParam() {
        log.info("unsatisfied request param");
        return Mono.empty();
    }

    @PostMapping("/web-exchange-bind")
    public Mono<Void> webExchangeBind(@RequestBody @Validated ProblemDetailRequest problemDetailRequest) {
        log.info("web exchange bind: {}", problemDetailRequest);
        return Mono.empty();
    }

    @GetMapping("/handler-method-validation-cookie")
    public Mono<Void> handlerMethodValidationCookie(@CookieValue @NotBlank(message = "cookie不能为空") String cookieValue) {
        log.info("cookieValue: {}", cookieValue);
        return Mono.empty();
    }

    @GetMapping("/handler-method-validation-matrix/{id}")
    public Mono<Void> handlerMethodValidationMatrix(@PathVariable String id,
                                                      @MatrixVariable @Size(max = 2, message = "list最大长度是2") List<String> list) {
        log.info("id: {}, list: {}", id, list);
        return Mono.empty();
    }

    @GetMapping("/handler-method-validation-model")
    public Mono<Void> handlerMethodValidationModel(@CheckPassword(message = "密码不能是空") ProblemDetailRequest problemDetailRequest) {
        log.info("problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    @GetMapping("/handler-method-validation-path/{id}")
    public Mono<Void> handlerMethodValidationPath(@PathVariable @Size(min = 5, message = "id长度至少5") String id) {
        log.info("id: {}", id);
        return Mono.empty();
    }

    @PostMapping("/handler-method-validation-body")
    public Mono<Void> handlerMethodValidationBody(@RequestBody @CheckPassword(message = "密码不能是空") ProblemDetailRequest problemDetailRequest) {
        log.info("problemDetailRequest: {}", problemDetailRequest);
        return Mono.empty();
    }

    @GetMapping("/handler-method-validation-header")
    public Mono<Void> handlerMethodValidationHeader(@RequestHeader @NotBlank(message = "header不能为空") String headerValue) {
        log.info("headerValue: {}", headerValue);
        return Mono.empty();
    }

    @GetMapping("/handler-method-validation-request-param")
    public Mono<Void> handlerMethodValidationRequestParam(@RequestParam @NotBlank(message = "参数不能为空") String param,
                                                            @RequestParam @Size(min = 5, message = "长度至少5") String value) {
        log.info("param: {}, value: {}", param, value);
        return Mono.empty();
    }

    @PostMapping("/handler-method-validation-request-part")
    public Mono<Void> handlerMethodValidationRequestPart(@RequestPart(required = false)
                                                         @CheckFilePart(requiredMessage = "文件不能为空") FilePart filePart) {
        log.info("part: {}", filePart);
        return Mono.empty();
    }

    @GetMapping("/handler-method-validation-other")
    public Mono<Void> handlerMethodValidationOther(
            @SessionAttribute(required = false) @NotBlank(message = "sessionAttribute 不能为空") String sessionAttribute,
            @RequestAttribute(required = false) @NotBlank(message = "requestAttribute 不能为空") String requestAttribute,
            @Value("") @NotBlank(message = "value 不能为空") String value) {
        log.info("sessionAttribute: {}, requestAttribute: {}, value: {}", sessionAttribute, requestAttribute, value);
        return Mono.empty();
    }

    @PostMapping("/handler-method-validation-request-body-validation-result")
    public Mono<Void> handlerMethodValidationRequestBodyValidationResult(@RequestBody List<@NotBlank(message = "元素不能包含空") String> list) {
        log.info("list: {}", list);
        return Mono.empty();
    }

    @GetMapping("/server-web-input")
    public Mono<Void> serverWebInput() {
        log.info("server web input");
        throw new ServerWebInputException("server web input error");
    }

    @GetMapping("/server-error")
    public Mono<Void> serverError() {
        log.info("server error");
        throw new ServerErrorException("server error", new RuntimeException());
    }

    @GetMapping("/response-status-exception")
    public Mono<Void> responseStatusException() {
        log.info("response status exception");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    @PostMapping("/content-too-large")
    public Mono<Void> contentTooLarge(@RequestBody byte[] body) {
        log.info("body.length: {}", body.length);
        return Mono.empty();
    }

    @GetMapping("/response-status-exception-invalid-api-version")
    public Mono<Void> responseStatusExceptionInvalidApiVersion() {
        log.info("response status exception invalid api version");
        return Mono.empty();
    }

    @GetMapping("/response-status-exception-missing-api-version")
    public Mono<Void> responseStatusExceptionMissingApiVersion() {
        log.info("response status exception missing api version");
        return Mono.empty();
    }

    @PostMapping("/payload-too-large")
    public Mono<Void> payloadTooLarge(@RequestBody byte[] body) {
        log.info("body.length: {}", body.length);
        throw new PayloadTooLargeException(new RuntimeException("payload too large"));
    }

    @GetMapping("/error-response")
    public Mono<Void> errorResponse() {
        log.info("error response");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setDetail("错误详情");
        extendedProblemDetail.setTitle("错误标题");
        extendedProblemDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        extendedProblemDetail.setErrors(Lists.newArrayList(new Error("错误信息1"), new Error("错误信息2")));
        throw new ErrorResponseException(HttpStatus.BAD_REQUEST,extendedProblemDetail,new RuntimeException("business exception"));
    }

    @GetMapping("/business")
    public Mono<Void> business() {
        log.info("business");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setTitle("支付失败标题");
        extendedProblemDetail.setDetail("支付失败详情");
        extendedProblemDetail.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        extendedProblemDetail.setErrors(Lists.newArrayList(new Error("余额不足"), new Error("支付频繁")));
        throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, extendedProblemDetail);
    }

    @GetMapping("/method-validation")
    public Mono<Void> methodValidation() {
        log.info("method validation");
        String result = problemDetailService.createProblemDetail("");
        log.info("result: {}", result);
        return Mono.empty();
    }
}
