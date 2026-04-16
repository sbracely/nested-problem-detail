package io.github.sbracely.extended.problem.detail.common.field.hide;

import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProblemDetailJacksonSerializerTest {

    @Test
    void shouldSerializeAllDefaultFieldsWhenNoVisibilityRuleIsConfigured() throws Exception {
        ObjectMapper objectMapper = objectMapper(ProblemDetailFieldVisibility.allowAll());
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.from(
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request content."),
                List.of(new io.github.sbracely.extended.problem.detail.common.response.Error(io.github.sbracely.extended.problem.detail.common.response.Error.Type.PARAMETER, "name", "must not be blank")));
        problemDetail.setInstance(URI.create("/test"));
        problemDetail.setProperty("traceId", "abc-123");

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(problemDetail));

        assertThat(json.path("title").asText()).isEqualTo("Bad Request");
        assertThat(json.path("status").asInt()).isEqualTo(400);
        assertThat(json.path("detail").asText()).isEqualTo("Invalid request content.");
        assertThat(json.path("instance").asText()).isEqualTo("/test");
        assertThat(json.path("errors")).isNotNull();
        assertThat(json.path("traceId").asText()).isEqualTo("abc-123");
    }

    @Test
    void shouldHideConfiguredFieldsForExtendedProblemDetail() throws Exception {
        ExtendedProblemDetailProperties.FieldVisibility fieldVisibility =
                new ExtendedProblemDetailProperties.FieldVisibility();
        fieldVisibility.getHide().add("detail");
        fieldVisibility.getHide().add("instance");

        ObjectMapper objectMapper = objectMapper(ProblemDetailFieldVisibility.from(fieldVisibility, List.of()));
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.from(
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request content."),
                List.of(new io.github.sbracely.extended.problem.detail.common.response.Error(Error.Type.PARAMETER, "name", "must not be blank")));
        problemDetail.setInstance(URI.create("/test"));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(problemDetail));

        assertThat(json.path("title").asText()).isEqualTo("Bad Request");
        assertThat(json.path("status").asInt()).isEqualTo(400);
        assertThat(json.path("errors")).isNotNull();
        assertThat(json.has("detail")).isFalse();
        assertThat(json.has("instance")).isFalse();
    }

    @Test
    void shouldHideConfiguredNestedErrorFields() throws Exception {
        ExtendedProblemDetailProperties.FieldVisibility fieldVisibility =
                new ExtendedProblemDetailProperties.FieldVisibility();
        fieldVisibility.getHide().add("errors.target");

        ObjectMapper objectMapper = objectMapper(ProblemDetailFieldVisibility.from(fieldVisibility, List.of()));
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.from(
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request content."),
                List.of(new io.github.sbracely.extended.problem.detail.common.response.Error(Error.Type.PARAMETER, "name", "must not be blank")));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(problemDetail));

        assertThat(json.path("errors")).hasSize(1);
        assertThat(json.path("errors").get(0).path("type").asText()).isEqualTo("PARAMETER");
        assertThat(json.path("errors").get(0).path("message").asText()).isEqualTo("must not be blank");
        assertThat(json.path("errors").get(0).has("target")).isFalse();
    }

    @Test
    void shouldHideConfiguredFieldsForPlainProblemDetail() throws Exception {
        ExtendedProblemDetailProperties.FieldVisibility fieldVisibility =
                new ExtendedProblemDetailProperties.FieldVisibility();
        fieldVisibility.getHide().add("status");
        fieldVisibility.getHide().add("detail");

        ObjectMapper objectMapper = objectMapper(ProblemDetailFieldVisibility.from(fieldVisibility, List.of()));
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method 'POST' is not supported.");
        problemDetail.setInstance(URI.create("/test"));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(problemDetail));

        assertThat(json.path("title").asText()).isEqualTo("Method Not Allowed");
        assertThat(json.path("instance").asText()).isEqualTo("/test");
        assertThat(json.has("status")).isFalse();
        assertThat(json.has("detail")).isFalse();
    }

    private static ObjectMapper objectMapper(ProblemDetailFieldVisibility fieldVisibility) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ProblemDetailJacksonSerializer(fieldVisibility));
        module.addSerializer(new ExtendedProblemDetailJacksonSerializer(fieldVisibility));
        return new ObjectMapper().rebuild()
                .addModule(module)
                .build();
    }
}
