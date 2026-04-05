package io.github.sbracely.extend.problem.detail.webmvc.example.controller;

import io.github.sbracely.extend.problem.detail.webmvc.example.exception.ExtendedErrorResponseException;
import io.github.sbracely.extend.problem.detail.webmvc.example.request.ProblemDetailRequest;
import io.github.sbracely.extend.problem.detail.webmvc.example.response.ProblemDetailResponse;
import io.github.sbracely.extend.problem.detail.webmvc.example.service.ProblemDetailService;
import io.github.sbracely.extend.problem.detail.webmvc.example.valid.annocation.CheckMultipartFile;
import io.github.sbracely.extend.problem.detail.webmvc.example.valid.annocation.CheckPassword;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
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
import org.springframework.web.accept.InvalidApiVersionException;
import org.springframework.web.accept.MissingApiVersionException;
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
public class MvcProblemDetailController {

    private static final Logger logger = LoggerFactory.getLogger(MvcProblemDetailController.class);

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final ProblemDetailService problemDetailService;

    public MvcProblemDetailController(RequestMappingHandlerMapping requestMappingHandlerMapping, ProblemDetailService problemDetailService) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.problemDetailService = problemDetailService;
    }

    /**
     * @see HttpRequestMethodNotSupportedException
     */
    @GetMapping("/http-request-method-not-supported-exception")
    public void httpRequestMethodNotSupportedException() {
        logger.info("httpRequestMethodNotSupportedException");
    }

    /**
     * @see HttpMediaTypeNotSupportedException
     */
    @PutMapping(path = "/http-media-type-not-supported-exception", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void httpMediaTypeNotSupportedException(ProblemDetailRequest problemDetailRequest) {
        logger.info("httpMediaTypeNotSupportedException, problemDetailRequest: {}", problemDetailRequest);
    }

    /**
     * @see HttpMediaTypeNotAcceptableException
     */
    @PutMapping(path = "/http-media-type-not-acceptable-exception", produces = MediaType.APPLICATION_JSON_VALUE)
    public void httpMediaTypeNotAcceptableException(ProblemDetailRequest problemDetailRequest) {
        logger.info("httpMediaTypeNotAcceptableException, problemDetailRequest: {}", problemDetailRequest);
    }

    /**
     * @see MissingPathVariableException
     */
    @DeleteMapping("/missing-path-variable-exception")
    public void missingPathVariableException(@PathVariable Integer id) {
        logger.info("missingPathVariableException, id: {}", id);
    }

    /**
     * @see MissingServletRequestParameterException
     */
    @GetMapping("/missing-servlet-request-parameter-exception")
    public void missingServletRequestParameterException(@RequestParam Integer id) {
        logger.info("missingServletRequestParameterException, id: {}", id);
    }

    /**
     * @see MissingServletRequestPartException
     */
    @PutMapping("/missing-servlet-request-part-exception")
    public void missingServletRequestPartException(@RequestPart MultipartFile file) {
        logger.info("missingServletRequestPartException, file: {}", file);
    }

    /**
     * @see ServletRequestBindingException
     */
    @GetMapping("/servlet-request-binding-exception")
    public void servletRequestBindingException() throws ServletRequestBindingException {
        logger.info("servletRequestBindingException");
        throw new ServletRequestBindingException("binding error");
    }

    /**
     * @see UnsatisfiedServletRequestParameterException
     */
    @GetMapping(path = "/unsatisfied-servlet-request-parameter-exception", params = {"type=1", "exist", "!debug"})
    public void unsatisfiedServletRequestParameterException() {
        logger.info("unsatisfiedServletRequestParameterException");
    }

    /**
     * @see org.springframework.web.bind.MissingRequestValueException
     */
    @GetMapping("/org-spring-web-bind-missing-request-value-exception")
    public void orgSpringWebBindMissingRequestValueException() throws org.springframework.web.bind.MissingRequestValueException {
        logger.info("orgSpringWebBindMissingRequestValueException");
        throw new org.springframework.web.bind.MissingRequestValueException("missing request value", true);
    }


    /**
     * @see MissingMatrixVariableException
     */
    @GetMapping("/missing-matrix-variable-exception/{id}")
    public void missingMatrixVariableException(@PathVariable String id, @MatrixVariable List<String> list) {
        logger.info("missingMatrixVariableException, id: {}, list: {}", id, list);
    }

    /**
     * @see MissingRequestCookieException
     */
    @GetMapping("/missing-request-cookie-exception")
    public void missingRequestCookieException(@CookieValue String cookieValue) {
        logger.info("missingRequestCookieException, cookieValue: {}", cookieValue);
    }

    /**
     * @see MissingRequestHeaderException
     */
    @GetMapping("/missing-request-header-exception")
    public void missingRequestHeaderException(@RequestHeader String header) {
        logger.info("missingRequestHeaderException, header: {}", header);
    }

    /**
     * @see MethodArgumentNotValidException
     */
    @PostMapping("/method-argument-not-valid-exception")
    public void methodArgumentNotValidException(@RequestBody @Validated ProblemDetailRequest problemDetailRequest) {
        logger.info("methodArgumentNotValidException, problemDetailRequest: {}", problemDetailRequest);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#cookieValue(CookieValue, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-cookie-value")
    public void handlerMethodValidationExceptionCookieValue(@CookieValue @Length(min = 2, message = "Name length must be at least 2") String name) {
        logger.info("handlerMethodValidationExceptionCookieValue, name: {}", name);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#matrixVariable(MatrixVariable, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-matrix-variable/{id}")
    public void handlerMethodValidationExceptionMatrixVariable(@PathVariable String id,
                                                               @MatrixVariable @Size(max = 2, message = "Maximum size is 2") List<String> list) {
        logger.info("handlerMethodValidationExceptionMatrixVariable, id: {}, list: {}", id, list);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#modelAttribute(ModelAttribute, ParameterErrors)
     */
    @GetMapping("/handler-method-validation-exception-model-attribute")
    public void handlerMethodValidationExceptionModelAttribute(@CheckPassword(message = "Password cannot be empty") ProblemDetailRequest problemDetailRequest) {
        logger.info("handlerMethodValidationExceptionModelAttribute, problemDetailRequest: {}", problemDetailRequest);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#pathVariable(PathVariable, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-path-variable/{id}")
    public void handlerMethodValidationExceptionPathVariable(@PathVariable @Length(min = 2, message = "ID minimum length is 2") String id) {
        logger.info("handlerMethodValidationExceptionPathVariable, id: {}", id);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBody(RequestBody, ParameterErrors)
     */
    @PostMapping("/handler-method-validation-exception-request-body")
    public void handlerMethodValidationExceptionRequestBody(@RequestBody @CheckPassword(message = "Password cannot be empty") ProblemDetailRequest problemDetailRequest) {
        logger.info("handlerMethodValidationExceptionRequestBody, problemDetailRequest: {}", problemDetailRequest);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestBodyValidationResult(RequestBody, ParameterValidationResult)
     */
    @PostMapping("/handler-method-validation-exception-request-body-validation-result")
    public void handlerMethodValidationExceptionRequestBodyValidationResult(@RequestBody List<@NotBlank(message = "Element cannot contain empty values") String> list) {
        logger.info("handlerMethodValidationExceptionRequestBodyValidationResult, list: {}", list);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestHeader(RequestHeader, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-request-header")
    public void handlerMethodValidationExceptionRequestHeader(@RequestHeader @Length(min = 2, message = "Minimum length is 2") String headerValue) {
        logger.info("handlerMethodValidationExceptionRequestHeader, headerValue: {}", headerValue);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestParam(RequestParam, ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-request-param")
    public void handlerMethodValidationExceptionRequestParam(@NotBlank(message = "Parameter cannot be empty") String param,
                                                             @NotNull(message = "Parameter 2 cannot be null") @NotBlank(message = "Parameter 2 cannot be blank") String param2) {
        logger.info("handlerMethodValidationExceptionRequestParam, param: {}, param2: {}", param, param2);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#requestPart(RequestPart, ParameterErrors)
     */
    @GetMapping("/handler-method-validation-exception-request-part")
    public void handlerMethodValidationExceptionRequestPart(@RequestPart(required = false) @CheckMultipartFile(extensionIncludeMessage = "File type not supported",
            extensionInclude = "txt", requiredMessage = "File cannot be empty") MultipartFile file) {
        logger.info("handlerMethodValidationExceptionRequestPart, file: {}", file);
    }

    /**
     * @see HandlerMethodValidationException
     * @see HandlerMethodValidationException.Visitor#other(ParameterValidationResult)
     */
    @GetMapping("/handler-method-validation-exception-other")
    public void handlerMethodValidationExceptionOther(@SessionAttribute(required = false) @NotBlank(message = "sessionAttribute cannot be empty")
                                                      String sessionAttribute,
                                                      @RequestAttribute(required = false) @NotBlank(message = "requestAttribute cannot be empty")
                                                      String requestAttribute,
                                                      @Value("") @NotBlank(message = "value cannot be empty") String value) {
        logger.info("handlerMethodValidationExceptionOther, sessionAttribute: {}, requestAttribute: {}, value: {}", sessionAttribute, requestAttribute, value);
    }

    /**
     * @see AsyncRequestTimeoutException
     */
    @GetMapping("/async-request-timeout-exception")
    public DeferredResult<Void> asyncRequestTimeoutException() {
        logger.info("asyncRequestTimeoutException");
        return new DeferredResult<>(1L);
    }

    /**
     * @see ErrorResponseException
     */
    @GetMapping("/error-response-exception")
    public void errorResponseException() {
        logger.info("errorResponseException");
        throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
    }

    /**
     * @see ExtendedErrorResponseException
     */
    @GetMapping("/extended-error-response-exception")
    public void extendedErrorResponseException() {
        logger.info("businessException");
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail();
        extendedProblemDetail.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        extendedProblemDetail.setDetail("Payment failed");
        extendedProblemDetail.setErrors(Arrays.asList(
                new Error(Error.Type.BUSINESS, null, "Insufficient balance"),
                new Error(Error.Type.BUSINESS, null, "Payment frequent")));
        throw new ExtendedErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, extendedProblemDetail);
    }

    /**
     * @see ResponseStatusException
     */
    @GetMapping("/response-status-exception")
    public void responseStatusException() {
        logger.info("responseStatusException");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exception");
    }

    /**
     * @see ServerWebInputException
     */
    @GetMapping("/server-web-input-exception")
    public void serverWebInputException() {
        logger.info("serverWebInputException");
        throw new ServerWebInputException("server web input error");
    }

    /**
     * @see UnsatisfiedRequestParameterException
     */
    @GetMapping(path = "/unsatisfied-request-parameter-exception", params = {"type=1", "exist", "!debug"})
    public void unsatisfiedRequestParameterException() {
        logger.info("unsatisfiedRequestParameterException");
    }

    /**
     * @see org.springframework.web.server.MissingRequestValueException
     */
    @GetMapping("/org-springframework-web-server-missing-request-value-exception")
    public void orgSpringframeworkWebServerMissingRequestValueException(HttpServletRequest httpServletRequest,
                                                                        String id) throws Exception {
        logger.info("missingRequestValueException, id: {}", id);
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
    @PostMapping("/web-exchange-bind-exception")
    public void webExchangeBindException(HttpServletRequest httpServletRequest,
                                         @RequestBody @Validated ProblemDetailRequest problemDetailRequest,
                                         BindingResult bindingResult) throws Exception {
        logger.info("webExchangeBindException, problemDetailRequest: {}", problemDetailRequest);
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
     * @see InvalidApiVersionException
     */
    @GetMapping("/invalid-api-version-exception")
    public void invalidApiVersionException() {
        logger.info("invalidApiVersionException");
    }

    /**
     * @see MissingApiVersionException
     */
    @GetMapping("/missing-api-version-exception")
    public void missingApiVersionException() {
        logger.info("missingApiVersionException");
    }

    /**
     * @see MethodNotAllowedException
     */
    @RequestMapping("/method-not-allowed-exception")
    public void methodNotAllowedException(HttpMethod httpMethod) {
        List<HttpMethod> supportedMethods = Arrays.asList(HttpMethod.GET, HttpMethod.POST);
        logger.info("methodNotAllowedException, httpMethod: {}", httpMethod);
        if (supportedMethods.contains(httpMethod)) {
            return;
        }
        throw new MethodNotAllowedException(httpMethod, supportedMethods);
    }

    /**
     * @see NotAcceptableStatusException
     */
    @GetMapping("/not-acceptable-status-exception")
    public void notAcceptableStatusException() {
        logger.info("notAcceptableStatusException");
        throw new NotAcceptableStatusException(List.of(MediaType.APPLICATION_JSON));
    }

    /**
     * @see ContentTooLargeException
     */
    @PostMapping("/content-too-large-exception")
    public void contentTooLargeException(@RequestPart MultipartFile file) {
        logger.info("contentTooLargeException, file: {}", file);
        throw new ContentTooLargeException(new RuntimeException("content too large"));
    }

    /**
     * @see UnsupportedMediaTypeStatusException
     */
    @PostMapping("/unsupported-media-type-status-exception")
    public void unsupportedMediaTypeStatusException() {
        logger.info("unsupportedMediaTypeStatusException");
        throw new UnsupportedMediaTypeStatusException("unsupported media type");
    }

    /**
     * @see ServerErrorException
     */
    @GetMapping("/server-error-exception")
    public void serverErrorException() {
        logger.info("serverErrorException");
        throw new ServerErrorException("server error", new RuntimeException());
    }

    /**
     * @see PayloadTooLargeException
     */
    @PostMapping("/payload-too-large-exception")
    public void payloadTooLargeException(@RequestPart MultipartFile file) {
        logger.info("payloadTooLargeException, file: {}", file);
        PayloadTooLargeException payloadTooLarge = new PayloadTooLargeException(new RuntimeException("payload too large"));
        payloadTooLarge.setDetail("payload too large");
        throw payloadTooLarge;
    }

    /**
     * @see MaxUploadSizeExceededException
     */
    @PostMapping("/max-upload-size-exceeded-exception")
    public void maxUploadSizeExceedededException(@RequestPart MultipartFile file) {
        logger.info("maxUploadSizeExceedededException, file: {}", file);
    }

    /**
     * @see ConversionNotSupportedException
     */
    @GetMapping("/conversion-not-supported-exception")
    public void conversionNotSupportedException(HttpServletRequest httpServletRequest, String data) throws Exception {
        logger.info("conversionNotSupportedException, data: {}", data);
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(httpServletRequest);
        if (null == handlerExecutionChain) {
            throw new MissingServletRequestParameterException("data", "String");
        }
        HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();
        ProblemDetailRequest problemDetailRequest = simpleTypeConverter.convertIfNecessary(data, ProblemDetailRequest.class, methodParameters[0]);
        logger.info("conversionNotSupportedException, problemDetailRequest: {}", problemDetailRequest);
    }

    /**
     * @see MethodArgumentConversionNotSupportedException
     */
    @GetMapping("/method-argument-conversion-not-supported-exception")
    public void methodArgumentConversionNotSupportedException(@RequestParam ProblemDetailRequest error) {
        logger.info("methodArgumentConversionNotSupportedException, error: {}", error);
    }

    /**
     * @see TypeMismatchException
     */
    @GetMapping("/type-mismatch-exception")
    public void typeMismatchException() {
        logger.info("typeMismatchException");
        throw new TypeMismatchException("test", Integer.class);
    }

    /**
     * @see MethodArgumentTypeMismatchException
     */
    @GetMapping("/method-argument-type-mismatch-exception")
    public void methodArgumentTypeMismatchException(Integer integer) {
        logger.info("methodArgumentTypeMismatchException, integer: {}", integer);
    }

    /**
     * @see HttpMessageNotReadableException
     */
    @PostMapping("/http-message-not-readable-exception")
    public void httpMessageNotReadableException(@RequestBody ProblemDetailRequest data) {
        logger.info("httpMessageNotReadableException, data: {}", data);
    }

    /**
     * @see HttpMessageNotWritableException
     * @see ProblemDetailResponseSerializer
     */
    @GetMapping("/http-message-not-writable-exception")
    public ProblemDetailResponse httpMessageNotWritableException() {
        logger.info("httpMessageNotWritableException");
        return new ProblemDetailResponse();
    }

    /**
     * @see MethodValidationException
     * @see MethodValidationConfiguration#validationPostProcessor()
     */
    @GetMapping("/method-validation-exception")
    public void methodValidationException() {
        logger.info("methodValidationException");
        ProblemDetailRequest problemDetailRequest = new ProblemDetailRequest();
        problemDetailRequest.setPassword("a");
        problemDetailRequest.setName("");
        problemDetailService.createProblemDetail(null, problemDetailRequest);
    }

    /**
     * @see AsyncRequestNotUsableException
     */
    @GetMapping("/async-request-not-usable-exception")
    public SseEmitter asyncRequestNotUsableException() {
        logger.info("asyncRequestNotUsableException");
        SseEmitter emitter = new SseEmitter(5000L);
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(100);
                    logger.info("asyncRequestNotUsableException, emitter send: {}", i);
                    emitter.send("event " + i);
                }
                emitter.complete();
            } catch (InterruptedException e) {
                logger.error("Thread sleep interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("Emitter error: {}", e.getClass().getSimpleName());
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}
