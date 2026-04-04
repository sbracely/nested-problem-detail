package io.github.sbracely.extended.problem.detail.core.response;

import org.jspecify.annotations.Nullable;

/**
 * Represents a detailed error information in the extended problem detail response.
 * <p>
 * This record encapsulates individual error details including error type, target,
 * and error message. It is used to provide detailed error information in API responses,
 * supporting both validation errors (parameter, cookie, header) and business logic errors.
 * </p>
 *
 * @param type    the type of error (PARAMETER, COOKIE, HEADER, or BUSINESS)
 * @param target  the target of the error, such as field name, resource name, or business entity
 * @param message the error message describing what went wrong
 * @see ExtendedProblemDetail
 * @since 1.0.0
 */
public record Error(@Nullable Type type, @Nullable String target, @Nullable String message) {

    /**
     * Enumeration of error types indicating the source of the error.
     * <ul>
     *     <li>{@code PARAMETER} - Error from request parameter</li>
     *     <li>{@code COOKIE} - Error from cookie value</li>
     *     <li>{@code HEADER} - Error from request header</li>
     *     <li>{@code BUSINESS} - Error from business logic</li>
     * </ul>
     */
    public enum Type {
        /** Error from request parameter or request body field. */
        PARAMETER,
        /** Error from cookie value. */
        COOKIE,
        /** Error from request header. */
        HEADER,
        /** Error from business logic. */
        BUSINESS,
    }
}
