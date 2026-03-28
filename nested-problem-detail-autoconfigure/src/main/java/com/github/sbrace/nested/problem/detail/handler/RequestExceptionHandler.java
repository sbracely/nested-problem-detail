package com.github.sbrace.nested.problem.detail.handler;

import com.github.sbrace.nested.problem.detail.NestedProblemDetailProperties;
import com.github.sbrace.nested.problem.detail.response.Error;
import com.github.sbrace.nested.problem.detail.response.ErrorCode;
import com.github.sbrace.nested.problem.detail.response.NestedProblemDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestExceptionHandler.class);

    private final NestedProblemDetailProperties properties;

    public RequestExceptionHandler(NestedProblemDetailProperties properties) {
        this.properties = properties;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Error> errors = ex.getBindingResult().getAllErrors().stream().map(Error::new).toList();
        NestedProblemDetail nestedProblemDetail = new NestedProblemDetail(ex.getBody());
        nestedProblemDetail.setErrors(errors);
        return handleExceptionInternal(ex, nestedProblemDetail, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Error> errorList = new ArrayList<>();
        ex.visitResults(new HandlerMethodValidationException.Visitor() {

            @Override
            public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.COOKIE, getParameterName(result));
            }

            @Override
            public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, getParameterName(result));
            }

            @Override
            public void modelAttribute(ModelAttribute modelAttribute, ParameterErrors errors) {
                processParameterErrors(errors);
            }

            @Override
            public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, getParameterName(result));
            }

            @Override
            public void requestBody(RequestBody requestBody, ParameterErrors errors) {
                processParameterErrors(errors);
            }

            @Override
            public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.HEADER, getParameterName(result));
            }

            @Override
            public void requestParam(RequestParam requestParam, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, getParameterName(result));
            }

            @Override
            public void requestPart(RequestPart requestPart, ParameterErrors errors) {
                processParameterErrors(errors);
            }

            @Override
            public void other(ParameterValidationResult result) {
                result.getResolvableErrors().forEach(error ->
                        log.error("codes: {}, defaultMessage: {}", error.getCodes(), error.getDefaultMessage()));
            }

            @Override
            public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, null);
            }

            private String getParameterName(ParameterValidationResult result) {
                return result.getMethodParameter().getParameterName();
            }

            private void processParameterValidationResult(ParameterValidationResult result,
                                                          Error.Type errorType,
                                                          String parameterName) {
                result.getResolvableErrors().stream().map(MessageSourceResolvable::getDefaultMessage)
                        .map(defaultMessage -> new Error(errorType, parameterName, defaultMessage))
                        .forEach(errorList::add);
            }

            private void processParameterErrors(ParameterErrors errors) {
                errors.getAllErrors().stream().map(Error::new).forEach(errorList::add);
            }
        });
        NestedProblemDetail nestedProblemDetail = new NestedProblemDetail(ex.getBody());
        nestedProblemDetail.setErrors(errorList);
        return handleExceptionInternal(ex, nestedProblemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(
            ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof WebExchangeBindException exchangeBindException) {
            ProblemDetail body = exchangeBindException.getBody();
            BindingResult bindingResult = exchangeBindException.getBindingResult();
            List<Error> errors = bindingResult.getAllErrors().stream().map(Error::new).toList();
            NestedProblemDetail nestedProblemDetail = new NestedProblemDetail(body);
            nestedProblemDetail.setErrors(errors);
            return handleExceptionInternal(ex, nestedProblemDetail, headers, status, request);
        }
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (null == body) {
            NestedProblemDetail nestedProblemDetail = new NestedProblemDetail();
            nestedProblemDetail.setErrorCode(ErrorCode.httpStatusCode(statusCode, properties.getErrorCodePrefix()));
            body = nestedProblemDetail;
        } else if (body instanceof NestedProblemDetail nestedProblemDetail) {
            if (null == nestedProblemDetail.getErrorCode()) {
                nestedProblemDetail.setErrorCode(ErrorCode.httpStatusCode(statusCode, properties.getErrorCodePrefix()));
            }
        } else if (body instanceof ProblemDetail problemDetail) {
            body = new NestedProblemDetail(problemDetail);
        }
        return super.createResponseEntity(body, headers, statusCode, request);
    }

    @Override
    protected ProblemDetail createProblemDetail(Exception ex, HttpStatusCode status, String defaultDetail, String detailMessageCode, Object[] detailMessageArguments, WebRequest request) {
        ProblemDetail problemDetail = super.createProblemDetail(ex, status, defaultDetail, detailMessageCode, detailMessageArguments, request);
        return new NestedProblemDetail(problemDetail);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestNotUsableException(
            AsyncRequestNotUsableException ex, WebRequest request) {
        log.error("handleAsyncRequestNotUsableException", ex);
        return null;
    }
}
