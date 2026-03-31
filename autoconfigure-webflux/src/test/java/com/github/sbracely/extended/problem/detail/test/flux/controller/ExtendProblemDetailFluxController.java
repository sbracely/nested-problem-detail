package com.github.sbracely.extended.problem.detail.test.flux.controller;

import com.github.sbracely.extended.problem.detail.response.Error;
import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.flux.exception.CustomizedException;
import com.github.sbracely.extended.problem.detail.test.flux.reuqest.ProblemDetailRequest;
import com.github.sbracely.extended.problem.detail.test.flux.reuqest.valid.annocation.CheckFilePart;
import com.github.sbracely.extended.problem.detail.test.flux.reuqest.valid.annocation.CheckPassword;
import com.github.sbracely.extended.problem.detail.test.flux.service.ProblemDetailService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/extend-problem-detail-flux")
public class ExtendProblemDetailFluxController {

    private final ProblemDetailService problemDetailService;

    public ExtendProblemDetailFluxController(ProblemDetailService problemDetailService) {
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
    public Mono<String> handlerMethodValidationCookie(@CookieValue @NotBlank(message = "cookie不能为空") String cookieValue) {
        log.info("cookieValue: {}", cookieValue);
        return Mono.just(cookieValue);
    }

    @GetMapping("/handler-method-validation-matrix/{id}")
    public Mono<String> handlerMethodValidationMatrix(@PathVariable String id,
                                                      @MatrixVariable @Size(max = 2, message = "list最大长度是2") List<String> list) {
        log.info("id: {}, list: {}", id, list);
        return Mono.just(id + list);
    }

    @GetMapping("/handler-method-validation-model")
    public Mono<ProblemDetailRequest> handlerMethodValidationModel(@CheckPassword(message = "密码不能是空") ProblemDetailRequest problemDetailRequest) {
        log.info("problemDetailRequest: {}", problemDetailRequest);
        return Mono.just(problemDetailRequest);
    }

    @GetMapping("/handler-method-validation-path/{id}")
    public Mono<String> handlerMethodValidationPath(@PathVariable @Size(min = 5, message = "id长度至少5") String id) {
        log.info("id: {}", id);
        return Mono.just(id);
    }

    @PostMapping("/handler-method-validation-body")
    public Mono<ProblemDetailRequest> handlerMethodValidationBody(@RequestBody @CheckPassword(message = "密码不能是空") ProblemDetailRequest problemDetailRequest) {
        log.info("problemDetailRequest: {}", problemDetailRequest);
        return Mono.just(problemDetailRequest);
    }

    @GetMapping("/handler-method-validation-header")
    public Mono<String> handlerMethodValidationHeader(@RequestHeader @NotBlank(message = "header不能为空") String headerValue) {
        log.info("headerValue: {}", headerValue);
        return Mono.just(headerValue);
    }

    @GetMapping("/handler-method-validation-request-param")
    public Mono<String> handlerMethodValidationRequestParam(@RequestParam @NotBlank(message = "参数不能为空") String param,
                                                            @RequestParam @Size(min = 5, message = "长度至少5") String value) {
        log.info("param: {}, value: {}", param, value);
        return Mono.just(param + value);
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

    @GetMapping("/server-web-input")
    public Mono<Void> serverWebInput() {
        log.info("server web input");
        return Mono.error(new ServerWebInputException("server web input error"));
    }



















    // HandlerMethodValidationException - RequestBodyValidationResult校验
    @PostMapping("/handler-method-validation-body-list")
    public Mono<List<String>> handlerMethodValidationBodyList(@RequestBody List<@NotBlank(message = "元素不能包含空") String> list) {
        log.info("list: {}", list);
        return Mono.just(list);
    }

    @GetMapping("/response-status-exception")
    public Mono<Void> responseStatusException() {
        log.info("response status exception");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    @GetMapping("/error-response-exception")
    public Mono<Void> errorResponseException() {
        log.info("error response exception");
        throw new org.springframework.web.ErrorResponseException(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/method-validation")
    public Mono<Void> methodValidation() {
        log.info("method validation");
        String result = problemDetailService.createProblemDetail("");
        log.info("result: {}", result);
        return Mono.empty();
    }

    @GetMapping("/customized")
    public Mono<Void> customized() {
        log.info("customized");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        extendedProblemDetail.setDetail("支付失败");
        extendedProblemDetail.setErrors(Lists.newArrayList(new Error("余额不足"), new Error("支付频繁")));
        throw new CustomizedException(HttpStatus.INTERNAL_SERVER_ERROR, extendedProblemDetail);
    }

    // ServerErrorException - 通过内部服务调用失败触发
    @GetMapping("/server-error")
    public Mono<String> serverError() {
        log.info("server error");
        return Mono.error(new ServerErrorException("server error", new RuntimeException()));
    }
}
