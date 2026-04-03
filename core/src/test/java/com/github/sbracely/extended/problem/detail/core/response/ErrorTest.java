package com.github.sbracely.extended.problem.detail.core.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Error} record.
 */
class ErrorTest {

    @Test
    void shouldCreateErrorWithAllFields() {
        Error error = new Error(Error.Type.PARAMETER, "email", "must be a valid email");

        assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(error.target()).isEqualTo("email");
        assertThat(error.message()).isEqualTo("must be a valid email");
    }

    @Test
    void shouldCreateErrorWithNullFields() {
        Error error = new Error(null, null, null);

        assertThat(error.type()).isNull();
        assertThat(error.target()).isNull();
        assertThat(error.message()).isNull();
    }

    @Test
    void shouldHaveCorrectTypeEnumValues() {
        assertThat(Error.Type.values()).containsExactly(
                Error.Type.PARAMETER,
                Error.Type.COOKIE,
                Error.Type.HEADER,
                Error.Type.BUSINESS
        );
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        Error error1 = new Error(Error.Type.PARAMETER, "field", "message");
        Error error2 = new Error(Error.Type.PARAMETER, "field", "message");
        Error error3 = new Error(Error.Type.HEADER, "field", "message");

        assertThat(error1).isEqualTo(error2);
        assertThat(error1).isNotEqualTo(error3);
        assertThat(error1.hashCode()).isEqualTo(error2.hashCode());
    }

    @Test
    void shouldHaveCorrectToString() {
        Error error = new Error(Error.Type.PARAMETER, "email", "invalid email");

        String str = error.toString();

        assertThat(str).contains("PARAMETER");
        assertThat(str).contains("email");
        assertThat(str).contains("invalid email");
    }
}
