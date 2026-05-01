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

    /**
     * Resolve a message code against the given {@link MessageSource}.
     * <p>
     * Codes wrapped in braces such as {@code {example.code}} are unwrapped before lookup so the same
     * helper can be used with validation-style placeholders and plain message codes. If no
     * {@link MessageSource} is available or the code cannot be resolved, the original input is
     * returned unchanged.
     *
     * @param code the message code to resolve, optionally wrapped in braces
     * @param messageSource the message source used for lookup
     * @param locale the locale to resolve the message for
     * @return the resolved message, or the original code when resolution is not possible
     */
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

    /**
     * Remove a single pair of surrounding braces from a message code.
     *
     * @param code the raw message code
     * @return the unwrapped code when the input is wrapped in braces; otherwise the original code
     */
    public static String unwrapCode(String code) {
        if (code.startsWith("{") && code.endsWith("}")) {
            return code.substring(1, code.length() - 1);
        }
        return code;
    }
}
