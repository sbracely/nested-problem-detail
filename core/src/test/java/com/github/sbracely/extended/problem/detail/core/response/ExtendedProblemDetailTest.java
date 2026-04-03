package com.github.sbracely.extended.problem.detail.core.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ExtendedProblemDetail} class.
 */
class ExtendedProblemDetailTest {

    @Test
    void shouldCreateEmptyExtendedProblemDetail() {
        ExtendedProblemDetail detail = new ExtendedProblemDetail();

        assertThat(detail.getErrors()).isNull();
    }

    @Test
    void shouldSetAndGetErrors() {
        ExtendedProblemDetail detail = new ExtendedProblemDetail();
        List<Error> errors = List.of(
                new Error(Error.Type.PARAMETER, "name", "must not be blank"),
                new Error(Error.Type.PARAMETER, "age", "must be positive")
        );

        detail.setErrors(errors);

        assertThat(detail.getErrors()).isEqualTo(errors);
    }

    @Test
    void shouldCreateFromProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(400);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail("Validation failed");
        problemDetail.setInstance(URI.create("/api/users"));

        ExtendedProblemDetail extendedDetail = new ExtendedProblemDetail(problemDetail);

        assertThat(extendedDetail.getStatus()).isEqualTo(400);
        assertThat(extendedDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(extendedDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(extendedDetail.getInstance()).isEqualTo(URI.create("/api/users"));
        assertThat(extendedDetail.getErrors()).isNull();
    }

    @Test
    void shouldCopyErrorsFromExtendedProblemDetail() {
        ExtendedProblemDetail original = new ExtendedProblemDetail();
        original.setStatus(400);
        original.setTitle("Bad Request");
        original.setErrors(List.of(
                new Error(Error.Type.PARAMETER, "field", "error message")
        ));

        ExtendedProblemDetail copy = new ExtendedProblemDetail(original);

        assertThat(copy.getErrors()).hasSize(1);
        assertThat(copy.getErrors()).containsExactly(
                new Error(Error.Type.PARAMETER, "field", "error message")
        );
    }

    @Test
    void shouldCreateIndependentCopyOfErrors() {
        ExtendedProblemDetail original = new ExtendedProblemDetail();
        original.setErrors(new java.util.ArrayList<>(List.of(
                new Error(Error.Type.PARAMETER, "field", "error")
        )));

        ExtendedProblemDetail copy = new ExtendedProblemDetail(original);

        // Modify original's errors list
        original.getErrors().add(new Error(Error.Type.HEADER, "header", "error"));

        // Copy should not be affected
        assertThat(copy.getErrors()).hasSize(1);
    }

    @Test
    void shouldHandleNullErrorsWhenCopying() {
        ExtendedProblemDetail original = new ExtendedProblemDetail();
        original.setStatus(400);
        // errors is null

        ExtendedProblemDetail copy = new ExtendedProblemDetail(original);

        assertThat(copy.getErrors()).isNull();
    }

    @Test
    void shouldIncludeErrorsInToString() {
        ExtendedProblemDetail detail = new ExtendedProblemDetail();
        detail.setStatus(400);
        detail.setTitle("Bad Request");
        detail.setErrors(List.of(
                new Error(Error.Type.PARAMETER, "name", "required")
        ));

        String str = detail.toString();

        assertThat(str).contains("errors");
        assertThat(str).contains("name");
        assertThat(str).contains("required");
    }
}
