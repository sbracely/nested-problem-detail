package io.github.sbracely.extended.problem.detail.common.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProblemDetailJacksonSerializerTest {

    @Test
    void shouldSerializeAllDefaultFieldsWhenNoVisibilityRuleIsConfigured() throws Exception {
        ObjectMapper objectMapper = objectMapper(ProblemDetailFieldVisibility.allowAll());
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.from(
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request content."),
                List.of(new Error(Error.Type.PARAMETER, "name", "must not be blank")));
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
                List.of(new Error(Error.Type.PARAMETER, "name", "must not be blank")));
        problemDetail.setInstance(URI.create("/test"));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(problemDetail));

        assertThat(json.path("title").asText()).isEqualTo("Bad Request");
        assertThat(json.path("status").asInt()).isEqualTo(400);
        assertThat(json.path("errors")).isNotNull();
        assertThat(json.has("detail")).isFalse();
        assertThat(json.has("instance")).isFalse();
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
