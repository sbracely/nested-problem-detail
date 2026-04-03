package com.github.sbracely.extended.problem.detail.core;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a detailed error information in the extended problem detail response.
 * <p>
 * This class encapsulates individual error details including error type, target,
 * and error message. It is used to provide detailed error information in API responses,
 * supporting both validation errors (parameter, cookie, header) and business logic errors.
 * </p>
 *
 * @see ExtendedProblemDetail
 * @since 0.0.1-SNAPSHOT
 */
public class Error {

    /**
     * The type of error (PARAMETER, COOKIE, HEADER, or BUSINESS).
     */
    @Nullable
    private Type type;

    /**
     * The target of the error, such as field name, resource name, or business entity.
     */
    @Nullable
    private String target;

    /**
     * The error message describing what went wrong.
     */
    @Nullable
    private String message;

    /**
     * Default constructor for creating an empty error.
     */
    public Error() {
    }

    /**
     * Constructs a fully populated error with type, message, and target.
     *
     * @param type    the type of error
     * @param target  the target of the error (field name, resource name, etc.)
     * @param message the error message
     */
    public Error(@Nullable Type type, @Nullable String target, @Nullable String message) {
        this.type = type;
        this.target = target;
        this.message = message;
    }

    /**
     * Gets the error type.
     *
     * @return the error type, or {@code null} if not set
     */
    public @Nullable Type getType() {
        return type;
    }

    /**
     * Sets the error type.
     *
     * @param type the error type to set
     */
    public void setType(@Nullable Type type) {
        this.type = type;
    }

    /**
     * Gets the error target.
     *
     * @return the target of the error, or {@code null} if not set
     */
    public @Nullable String getTarget() {
        return target;
    }

    /**
     * Sets the error target.
     *
     * @param target the target to set (field name, resource name, etc.)
     */
    public void setTarget(@Nullable String target) {
        this.target = target;
    }

    /**
     * Gets the error message.
     *
     * @return the error message, or {@code null} if not set
     */
    public @Nullable String getMessage() {
        return message;
    }

    /**
     * Sets the error message.
     *
     * @param message the error message to set
     */
    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Override
    public final boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Error error = (Error) o;
        return type == error.type && Objects.equals(target, error.target) && Objects.equals(message, error.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, message);
    }

    @Override
    public String toString() {
        return "Error{" +
                "type=" + type +
                ", target='" + target + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

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
        PARAMETER,
        COOKIE,
        HEADER,
        BUSINESS,
    }
}
