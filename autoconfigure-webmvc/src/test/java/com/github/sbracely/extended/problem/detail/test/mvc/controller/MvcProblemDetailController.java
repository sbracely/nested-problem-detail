package com.github.sbracely.extended.problem.detail.test.mvc.controller;

import com.github.sbracely.extended.problem.detail.response.Error;
import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.mvc.exception.CustomizedException;
import com.github.sbracely.extended.problem.detail.test.mvc.response.ProblemDetailResponse;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.ProblemDetailRequest;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.annocation.CheckMultipartFile;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.annocation.CheckPassword;
import com.github.sbracely.extended.problem.detail.test.mvc.service.ProblemDetailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.*;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/mvc-problem-detail")
public class MvcProblemDetailController {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final ProblemDetailService problemDetailService;

    public MvcProblemDetailController(RequestMappingHandlerMapping requestMappingHandlerMapping, ProblemDetailService problemDetailService) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.problemDetailService = problemDetailService;
    }

    @GetMapping("/param")
    public void get(@RequestParam Integer id) {
        log.info("id: {}", id);
    }

    @PutMapping(path = "/consume-json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void putConsumeJson(ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @PutMapping(path = "/produce-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void putProduceJson(ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer iid) {
        log.info("iid: {}", iid);
    }

    @PutMapping("/file")
    public void file(@RequestPart MultipartFile file) {
        log.info("file: {}", file);
    }

    @GetMapping("/matrix/{id}")
    public void matrix(@PathVariable String id, @MatrixVariable List<String> list) {
        log.info("id: {}, list: {}", id, list);
    }

    @GetMapping("/cookie")
    public void cookie(@CookieValue String cookieValue) {
        log.info("cookieValue: {}", cookieValue);
    }

    @GetMapping("/header")
    public void header(@RequestHeader String header) {
        log.info("header: {}", header);
    }

    @GetMapping(path = "/unsatisfied", params = {"type=1", "exist", "!debug"})
    public void unsatisfied() {
        log.info("unsatisfied");
    }

    @PostMapping("/create")
    public void create(@RequestBody @Validated ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @GetMapping(value = "/cookie-value")
    public String cookieValue(@CookieValue @Length(min = 2, message = "姓名长度最小是 2") String name) {
        log.info("name: {}", name);
        return name;
    }

    @GetMapping("/matrix-variable/{id}")
    public void matrixVariable(@PathVariable String id,
                               @MatrixVariable @Size(max = 2, message = "最大长度是 2") List<String> list) {
        log.info("id: {}, list: {}", id, list);
    }

    @GetMapping("/model-attribute")
    public void modelAttribute(@CheckPassword(message = "密码不能是空") ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @GetMapping("/path-variable/{id}")
    public void pathVariable(@PathVariable @Length(min = 2, message = "id 最小长度是 2") String id) {
        log.info("id: {}", id);
    }

    @PostMapping("/request-body")
    public void requestBody(@RequestBody @CheckPassword(message = "密码不能是空") ProblemDetailRequest problemDetailRequest) {
        log.info("problemDetailRequest: {}", problemDetailRequest);
    }

    @GetMapping("/request-header")
    public void requestHeader(@RequestHeader @Length(min = 2, message = "最小长度是 2") String headerValue) {
        log.info("headerValue: {}", headerValue);
    }

    @GetMapping("/request-param")
    public void requestParam(@NotBlank(message = "参数不能为空") String param,
                             @NotNull(message = "参数2不能为空") @NotNull(message = "参数2不能为null") String param2) {
        log.info("param: {}, param2: {}", param, param2);
    }

    @GetMapping("/request-part")
    public void requestPart(@RequestPart(required = false) @CheckMultipartFile(extensionIncludeMessage = "文件类型不支持",
            extensionInclude = "txt", requiredMessage = "文件不能为空") MultipartFile file) {
        log.info("file: {}", file);
    }

    @GetMapping("/request-other")
    public void requestOther(@SessionAttribute(required = false) @NotBlank(message = "sessionAttribute 不能为空")
                             String sessionAttribute,
                             @RequestAttribute(required = false) @NotBlank(message = "requestAttribute 不能为空")
                             String requestAttribute,
                             @Value("") @NotBlank(message = "value 不能为空") String value) {
        log.info("sessionAttribute: {}, requestAttribute: {}, value: {}", sessionAttribute, requestAttribute, value);
    }

    @PostMapping("/request-body-validation-result")
    public void requestBodyValidationResult(@RequestBody List<@NotBlank(message = "元素不能包含空") String> list) {
        log.info("list.size = {}, list: {}", list.size(), list);
    }

    @GetMapping("/async-request-timeout")
    public DeferredResult<Void> asyncRequestTimeoutException() {
        return new DeferredResult<>(1L);
    }


    @PostMapping("/content-too-large")
    public void contentTooLarge(@RequestPart MultipartFile file) {
        log.info("file: {}", file);
        throw new ContentTooLargeException(new RuntimeException("content too large"));
    }

    @RequestMapping("/method-not-allowed")
    public void methodNotAllowed(HttpMethod httpMethod) {
        List<HttpMethod> supportedMethods = Arrays.asList(HttpMethod.GET, HttpMethod.POST);
        log.info("httpMethod: {}", httpMethod);
        if (supportedMethods.contains(httpMethod)) {
            return;
        }
        throw new MethodNotAllowedException(httpMethod, supportedMethods);
    }

    @GetMapping("/missing-request-value")
    public void missingRequestValue(String id) throws Exception {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            throw new MissingServletRequestParameterException("id", "String");
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
        if (null == handlerExecutionChain) {
            throw new MissingServletRequestParameterException("id", "String");
        }
        HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        throw new MissingRequestValueException("id", String.class, "request param", methodParameters[0]);
    }

    @GetMapping(path = "/api-version")
    public void apiVersion() {
        log.info("apiVersion");
    }

    @GetMapping("/not-acceptable-status")
    public void notAcceptableStatus() {
        throw new NotAcceptableStatusException(List.of(MediaType.APPLICATION_JSON));
    }

    @PostMapping("/file-max-size")
    public void fileMaxSize(@RequestPart MultipartFile file) {
        log.info("file.size: {}", file.getSize());
    }

    @PostMapping("/payload-too-large")
    public void payloadTooLarge(@RequestPart MultipartFile file) {
        log.info("file: {}", file);
        PayloadTooLargeException payloadTooLarge = new PayloadTooLargeException(new RuntimeException("payload too large"));
        payloadTooLarge.setDetail("payload too large");
        throw payloadTooLarge;
    }

    @GetMapping("/server-error")
    public void serverError() {
        log.info("server error");
        throw new ServerErrorException("server error", new RuntimeException());
    }

    @GetMapping("/server-web-input")
    public void serverWebInput() {
        log.info("server web input");
        throw new ServerWebInputException("server web input error");
    }

    @PostMapping("/unsupported-media-type")
    public void unsupportedMediaType() {
        log.info("unsupported media type");
        throw new UnsupportedMediaTypeStatusException("unsupported media type");
    }

    @PostMapping("/web-exchange-bind")
    public void webExchangeBind(@RequestBody @Validated ProblemDetailRequest problemDetailRequest, BindingResult bindingResult) throws Exception {
        log.info("web exchange bind");
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            throw new MissingServletRequestParameterException("id", "String");
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
        if (null == handlerExecutionChain) {
            throw new MissingServletRequestParameterException("id", "String");
        }
        HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        throw new WebExchangeBindException(methodParameters[0], bindingResult);
    }

    @GetMapping("/missing-request-value-mvc")
    public void missingRequestValueMvc(String id) throws org.springframework.web.bind.MissingRequestValueException {
        throw new org.springframework.web.bind.MissingRequestValueException("id is required", true);
    }

    @GetMapping("/servlet-request-binding")
    public void servletRequestBinding() throws ServletRequestBindingException {
        log.info("servlet request binding");
        throw new ServletRequestBindingException("binding error");
    }

    @GetMapping("/unsatisfied-request-param")
    public void unsatisfiedRequestParam() {
        log.info("unsatisfied request param");
        throw new UnsatisfiedRequestParameterException(
                List.of("type=1", "exist"),
                org.springframework.util.CollectionUtils.toMultiValueMap(Map.of())
        );
    }

    @GetMapping("/error-response-exception")
    public void errorResponseException() {
        throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/response-status-exception")
    public void responseStatusException() {
        log.info("response status exception");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    @GetMapping("/conversion-not-supported")
    public void conversionNotSupported(String data) throws Exception {
        log.info("data: {}", data);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            throw new MissingServletRequestParameterException("data", "String");
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
        if (null == handlerExecutionChain) {
            throw new MissingServletRequestParameterException("data", "String");
        }
        HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();
        ProblemDetailRequest problemDetailRequest = simpleTypeConverter.convertIfNecessary(data, ProblemDetailRequest.class, methodParameters[0]);
        log.info("problemDetailRequest: {}", problemDetailRequest);
    }

    @GetMapping("/method-argument-conversion-not-supported")
    public void methodArgumentConversionNotSupported(@RequestParam ProblemDetailRequest error) {
        log.info("error: {}", error);
    }

    @GetMapping("/type-mismatch-exception")
    public void typeMismatchException() {
        log.info("type mismatch exception");
        throw new TypeMismatchException("test", Integer.class);
    }

    @GetMapping("/method-argument-type-mismatch")
    public void methodArgumentTypeMismatch(Integer integer) {
        log.info("integer: {}", integer);
    }

    @PostMapping("/http-message-not-readable")
    public void httpMessageNotReadable(@RequestBody ProblemDetailRequest data) {
        log.info("data: {}", data);
    }

    @GetMapping(path = "/http-message-not-writable")
    public ProblemDetailResponse httpMessageNotWritable() {
        return new ProblemDetailResponse();
    }

    @GetMapping("/method-validation")
    public void methodValidation() {
        String problemDetail = problemDetailService.createProblemDetail("");
        log.info("problemDetail: {}", problemDetail);
    }

    @GetMapping("/async-request-not-usable")
    public SseEmitter asyncRequestNotUsable() {
        SseEmitter emitter = new SseEmitter(5000L);
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(100);
                    log.info("emitter send: {}", i);
                    emitter.send("event " + i);
                }
                emitter.complete();
            } catch (InterruptedException e) {
                log.error("Thread sleep interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Emitter error: {}", e.getClass().getSimpleName());
                emitter.completeWithError(e);
            }
        });
        return emitter;
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
}
