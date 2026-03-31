package com.github.sbracely.extended.problem.detail.test.flux.controller;

import com.github.sbracely.extended.problem.detail.response.Error;
import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.flux.exception.CustomizedException;
import com.github.sbracely.extended.problem.detail.test.flux.reuqest.ProblemDetailRequest;
import com.github.sbracely.extended.problem.detail.test.flux.reuqest.valid.annocation.CheckPassword;
import com.github.sbracely.extended.problem.detail.test.flux.service.ProblemDetailService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
    public void methodNotAllowed() {
        log.info("method-not-allowed");
    }

    @GetMapping(path = "/not-acceptable-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public void notAcceptableStatus() {
        log.info("not-acceptable-status");
    }

    @PostMapping(path = "/unsupported-media-type", consumes = MediaType.APPLICATION_XML_VALUE)
    public void unsupportedMediaType() {
        log.info("unsupported media type");
    }

    @GetMapping("/missing-request-value")
    public void missingRequestValue(@RequestParam String id) {
        log.info("missing-request-value id: {}", id);
    }

    @GetMapping(path = "/unsatisfied-request-param", params = {"type=1", "exist", "!debug"})
    public void unsatisfiedRequestParam() {
        log.info("unsatisfied request param");
    }

    @PostMapping("/web-exchange-bind")
    public void webExchangeBind(@RequestBody @Validated ProblemDetailRequest problemDetailRequest) {
        log.info("web exchange bind: {}", problemDetailRequest);
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

    @GetMapping("/handler-method-validation-request-param")
    public Mono<String> handlerMethodValidationRequestParam(@RequestParam @NotBlank(message = "参数不能为空") String param,
                                                @RequestParam @Size(min = 5, message = "长度至少5") String value) {
        log.info("param: {}, value: {}", param, value);
        return Mono.just(param + value);
    }

    @PostMapping("/handler-method-validation-request-part")
    public Mono<Void> handlerMethodValidationRequestPart(@RequestPart Mono<FilePart> filePartMono) {
        log.info("part: {}", filePartMono);
        return Mono.empty();
    }
















    // HandlerMethodValidationException - RequestHeader校验
    @GetMapping("/handler-method-validation-header")
    public Mono<String> handlerMethodValidationHeader(@RequestHeader @NotBlank(message = "header不能为空") String headerValue) {
        log.info("headerValue: {}", headerValue);
        return Mono.just(headerValue);
    }



    // HandlerMethodValidationException - RequestBodyValidationResult校验
    @PostMapping("/handler-method-validation-body-list")
    public Mono<List<String>> handlerMethodValidationBodyList(@RequestBody List<@NotBlank(message = "元素不能包含空") String> list) {
        log.info("list: {}", list);
        return Mono.just(list);
    }

    @GetMapping("/response-status-exception")
    public void responseStatusException() {
        log.info("response status exception");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    @GetMapping("/error-response-exception")
    public void errorResponseException() {
        log.info("error response exception");
        throw new org.springframework.web.ErrorResponseException(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/method-validation")
    public void methodValidation() {
        log.info("method validation");
        String result = problemDetailService.createProblemDetail("");
        log.info("result: {}", result);
    }

    @GetMapping("/customized")
    public void customized() {
        log.info("customized");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        extendedProblemDetail.setDetail("支付失败");
        extendedProblemDetail.setErrors(Lists.newArrayList(new Error("余额不足"), new Error("支付频繁")));
        throw new CustomizedException(HttpStatus.INTERNAL_SERVER_ERROR, extendedProblemDetail);
    }


    // ServerWebInputException的子类 - 通过类型转换失败触发
    @GetMapping("/server-web-input")
    public Mono<Integer> serverWebInput(@RequestParam Integer number) {
        log.info("number: {}", number);
        return Mono.just(number);
    }

    // ServerErrorException - 通过内部服务调用失败触发
    @GetMapping("/server-error")
    public Mono<String> serverError() {
        log.info("server error");
        return Mono.error(new ServerErrorException("server error", new RuntimeException()));
    }
}
