package io.github.sbracely.extended.problem.detail.common.error.resolver;

import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

/**
 * Resolves message codes into {@link ExtendedProblemDetail} extension fields.
 */
public final class ExtendedProblemDetailMessageResolver {

    private ExtendedProblemDetailMessageResolver() {
    }

    public static @Nullable String message(@Nullable String code, @Nullable MessageSource messageSource, Locale locale) {
        if (code == null || messageSource == null) {
            return code;
        }
        String unwrapCode = unwrapCode(code);
        try {
            return messageSource.getMessage(unwrapCode, null, locale);
        } catch (NoSuchMessageException ex) {
            return code;
        }
    }

    public static String unwrapCode(String code) {
        if (code.startsWith("{") && code.endsWith("}")) {
            return code.substring(1, code.length() - 1);
        }
        return code;
    }
}
