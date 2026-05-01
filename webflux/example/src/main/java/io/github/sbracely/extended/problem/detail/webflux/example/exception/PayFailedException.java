package io.github.sbracely.extended.problem.detail.webflux.example.exception;

import io.github.sbracely.extended.problem.detail.common.response.Error;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.Locale;

import static io.github.sbracely.extended.problem.detail.common.error.resolver.ExtendedProblemDetailMessageResolver.message;

public class PayFailedException extends ErrorResponseException {

    private static final String DEFAULT_TITLE = "Payment failed";
    private static final String DEFAULT_DETAIL = "The payment request could not be processed.";

    private final List<String> errorMessageCodes;
    private final String errorsPropertyName;

    public PayFailedException(List<String> errorMessageCodes, String errorsPropertyName) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_DETAIL),
                null
        );
        this.errorMessageCodes = List.copyOf(errorMessageCodes);
        this.errorsPropertyName = errorsPropertyName;
        setTitle(DEFAULT_TITLE);
    }

    @Override
    public ProblemDetail updateAndGetBody(@Nullable MessageSource messageSource, Locale locale) {
        ProblemDetail body = super.updateAndGetBody(messageSource, locale);

        List<Error> localizedErrors = errorMessageCodes.stream()
                .map(code -> new Error(
                        Error.Type.BUSINESS,
                        null,
                        message(code, messageSource, locale)
                ))
                .toList();

        body.setProperty(errorsPropertyName, localizedErrors);
        return body;
    }
}
