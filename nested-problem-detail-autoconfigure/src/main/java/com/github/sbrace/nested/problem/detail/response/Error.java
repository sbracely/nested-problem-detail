package com.github.sbrace.nested.problem.detail.response;

import org.jspecify.annotations.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Objects;

public class Error {
    @Nullable
    private Type type;
    @Nullable
    private String field;
    @Nullable
    private String message;

    public Error() {
    }

    public Error(Type type, String field, String message) {
        this.type = type;
        this.field = field;
        this.message = message;
    }

    public Error(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError) {
            this.field = fieldError.getField();
        }
        this.message = objectError.getDefaultMessage();
        this.type = Type.PARAMETER;
    }

    public @Nullable Type getType() {
        return type;
    }

    public void setType(@Nullable Type type) {
        this.type = type;
    }

    public @Nullable String getField() {
        return field;
    }

    public void setField(@Nullable String field) {
        this.field = field;
    }

    public @Nullable String getMessage() {
        return message;
    }

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
        return type == error.type && Objects.equals(field, error.field) && Objects.equals(message, error.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field, message);
    }

    public enum Type {
        PARAMETER,
        COOKIE,
        HEADER,
    }
}
