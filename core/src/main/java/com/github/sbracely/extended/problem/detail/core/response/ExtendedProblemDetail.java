package com.github.sbracely.extended.problem.detail.core.response;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ProblemDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Extended Problem Detail that includes additional error information.
 * <p>
 * This class extends Spring Framework's {@link ProblemDetail} to add support for
 * including a list of detailed {@link Error} objects. It provides field-level
 * validation error information in API responses, making it easier for API consumers
 * to understand and fix validation issues.
 * </p>
 * <p>
 * The extended problem detail follows RFC 9457 (Problem Details for HTTP APIs)
 * while adding custom extensions for detailed error information.
 * </p>
 *
 * @see Error
 * @see ProblemDetail
 * @since 0.0.1-SNAPSHOT
 */
public class ExtendedProblemDetail extends ProblemDetail {

    /**
     * List of detailed error information.
     */
    @Nullable
    private List<Error> errors;

    /**
     * Default constructor creating an empty extended problem detail.
     */
    public ExtendedProblemDetail() {
    }

    /**
     * Constructs an extended problem detail from an existing ProblemDetail.
     * <p>
     * If the provided ProblemDetail is already an ExtendedProblemDetail with errors,
     * those errors are copied to the new instance.
     * </p>
     *
     * @param problemDetail the ProblemDetail to copy properties from
     */
    public ExtendedProblemDetail(ProblemDetail problemDetail) {
        super(problemDetail);
        if (problemDetail instanceof ExtendedProblemDetail extendedProblemDetail) {
            if (extendedProblemDetail.errors != null) {
                this.errors = new ArrayList<>(extendedProblemDetail.errors);
            }
        }
    }

    /**
     * Gets the list of detailed errors.
     *
     * @return the list of errors, or {@code null} if not set
     */
    public @Nullable List<Error> getErrors() {
        return errors;
    }

    /**
     * Sets the list of detailed errors.
     *
     * @param errors the list of errors to set
     */
    public void setErrors(@Nullable List<Error> errors) {
        this.errors = errors;
    }

    @Override
    protected String initToStringContent() {
        return super.initToStringContent() +
                ", errors=" + errors;
    }
}
