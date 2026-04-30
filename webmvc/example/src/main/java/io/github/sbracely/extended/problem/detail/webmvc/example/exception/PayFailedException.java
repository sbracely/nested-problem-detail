package io.github.sbracely.extended.problem.detail.webmvc.example.exception;

import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.Locale;

import static io.github.sbracely.extended.problem.detail.common.error.resolver.ExtendedProblemDetailMessageResolver.message;

public class PayFailedException extends ErrorResponseException {

    private final List<String> errorMessageCodes;

    public PayFailedException(List<String> errorMessageCodes) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, null);
        this.errorMessageCodes = List.copyOf(errorMessageCodes);
        setDetail("Payment failed");
    }

    @Override
    public ProblemDetail updateAndGetBody(@Nullable MessageSource messageSource, Locale locale) {
        ProblemDetail body = super.updateAndGetBody(messageSource, locale);

        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(body);
        List<Error> localizedErrors = errorMessageCodes.stream()
                .map(code -> new Error(Error.Type.BUSINESS, null, message(code, messageSource, locale)))
                .toList();

        extendedProblemDetail.setErrors(localizedErrors);
        return extendedProblemDetail;
    }
}
