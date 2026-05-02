package io.github.sbracely.extended.problem.detail.webflux.example.open.api;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Operation trigger fixtures for the WebFlux OpenAPI strict contract tests.
 * <p>
 * Each entry maps an {@code operationId} (as documented in {@code /v3/api-docs}) to a
 * {@link FluxOperationFixture} that captures the exact HTTP request needed to trigger the
 * documented error response.
 */
public final class FluxOperationFixtures {

    private static final String BASE = "/flux-extended-problem-detail";

    private FluxOperationFixtures() {
    }

    /**
     * All documented WebFlux operation fixtures keyed by {@code operationId}.
     */
    public static Map<String, FluxOperationFixture> all() {
        Map<String, FluxOperationFixture> map = new LinkedHashMap<>();

        // ── default scenario ──────────────────────────────────────────────────────────
        map.put("methodNotAllowedException",
                new FluxOperationFixture("default",
                        BASE + "/method-not-allowed-exception", "get",
                        client -> client.delete().uri(BASE + "/method-not-allowed-exception").exchange(),
                        405));

        map.put("notAcceptableStatusException",
                new FluxOperationFixture("default",
                        BASE + "/not-acceptable-status-exception", "get",
                        client -> client.get().uri(BASE + "/not-acceptable-status-exception")
                                .header("Accept", "application/xml").exchange(),
                        406));

        map.put("unsupportedMediaTypeStatusException",
                new FluxOperationFixture("default",
                        BASE + "/unsupported-media-type-status-exception", "post",
                        client -> client.post().uri(BASE + "/unsupported-media-type-status-exception").exchange(),
                        415));

        map.put("extendedErrorResponseException",
                new FluxOperationFixture("default",
                        BASE + "/extended-error-response-exception", "get",
                        client -> client.get().uri(BASE + "/extended-error-response-exception").exchange(),
                        500));

        map.put("errorResponseException",
                new FluxOperationFixture("default",
                        BASE + "/error-response-exception", "get",
                        client -> client.get().uri(BASE + "/error-response-exception").exchange(),
                        400));

        map.put("responseStatusException",
                new FluxOperationFixture("default",
                        BASE + "/response-status-exception", "get",
                        client -> client.get().uri(BASE + "/response-status-exception").exchange(),
                        400));

        map.put("missingRequestValueException",
                new FluxOperationFixture("default",
                        BASE + "/missing-request-value-exception", "get",
                        client -> client.get().uri(BASE + "/missing-request-value-exception").exchange(),
                        400));

        map.put("unsatisfiedRequestParameterException",
                new FluxOperationFixture("default",
                        BASE + "/unsatisfied-request-parameter-exception", "get",
                        client -> client.get().uri(BASE + "/unsatisfied-request-parameter-exception")
                                .exchange(),
                        400));

        map.put("contentTooLargeException",
                new FluxOperationFixture("default",
                        BASE + "/content-too-large-exception", "post",
                        client -> client.post().uri(BASE + "/content-too-large-exception")
                                .bodyValue("x".repeat(1024 * 1024)).exchange(),
                        413));

        map.put("payloadTooLargeException",
                new FluxOperationFixture("default",
                        BASE + "/payload-too-large-exception", "post",
                        client -> client.post().uri(BASE + "/payload-too-large-exception")
                                .bodyValue("text").exchange(),
                        413));

        map.put("serverWebInputException",
                new FluxOperationFixture("default",
                        BASE + "/server-web-input-exception", "get",
                        client -> client.get().uri(BASE + "/server-web-input-exception").exchange(),
                        400));

        map.put("noResourceFoundException",
                new FluxOperationFixture("default",
                        BASE + "/no-resource-found", "get",
                        client -> client.get().uri(BASE + "/no-resource-found").exchange(),
                        404));

        map.put("serverErrorException",
                new FluxOperationFixture("default",
                        BASE + "/server-error-exception", "get",
                        client -> client.get().uri(BASE + "/server-error-exception").exchange(),
                        500));

        map.put("webExchangeBindException",
                new FluxOperationFixture("default",
                        BASE + "/web-exchange-bind-exception", "post",
                        client -> client.post().uri(BASE + "/web-exchange-bind-exception")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"name\":\"abc\",\"password\":\"123\",\"address\":{\"street\":\"\"}}").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionCookieValue",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-cookie-value", "get",
                        client -> client.get().uri(BASE + "/handler-method-validation-exception-cookie-value")
                                .cookie("cookieValue", "").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionMatrixVariable",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-matrix/{id}", "get",
                        client -> client.get()
                                .uri(BASE + "/handler-method-validation-exception-matrix/abc;list=a,b,c")
                                .exchange(),
                        400));

        map.put("handlerMethodValidationExceptionModelAttribute",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-model-attribute", "get",
                        client -> client.get()
                                .uri(BASE + "/handler-method-validation-exception-model-attribute").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionPathVariable",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-path-variable/{id}", "get",
                        client -> client.get()
                                .uri(BASE + "/handler-method-validation-exception-path-variable/abc")
                                .exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestBody",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-body", "post",
                        client -> client.post()
                                .uri(BASE + "/handler-method-validation-exception-request-body")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"name\":\"abc\"}").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestBodyValidationResult",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-body-validation-result", "post",
                        client -> client.post()
                                .uri(BASE + "/handler-method-validation-exception-request-body-validation-result")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(java.util.List.of("", "a")).exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestHeader",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-header", "get",
                        client -> client.get()
                                .uri(BASE + "/handler-method-validation-exception-request-header")
                                .header("headerValue", "").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestParam",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-param", "get",
                        client -> client.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path(BASE + "/handler-method-validation-exception-request-param")
                                        .queryParam("param", "")
                                        .queryParam("value", "ab")
                                        .build())
                                .exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestPart",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-part", "post",
                        client -> client.post()
                                .uri(BASE + "/handler-method-validation-exception-request-part")
                                .bodyValue(java.util.Collections.emptyMap()).exchange(),
                        400));

        map.put("handlerMethodValidationExceptionOther",
                new FluxOperationFixture("default",
                        BASE + "/handler-method-validation-exception-other", "get",
                        client -> client.get()
                                .uri(BASE + "/handler-method-validation-exception-other").exchange(),
                        400));

        map.put("methodValidationException",
                new FluxOperationFixture("default",
                        BASE + "/method-validation-exception", "get",
                        client -> client.get().uri(BASE + "/method-validation-exception").exchange(),
                        500));

        // ── api-version scenario ──────────────────────────────────────────────────────
        map.put("invalidApiVersionException",
                new FluxOperationFixture("api-version",
                        BASE + "/invalid-api-version-exception", "get",
                        null, // trigger requires api-version properties – see FluxOpenApiApiVersionContractTests
                        400));

        map.put("missingApiVersionException",
                new FluxOperationFixture("api-version",
                        BASE + "/missing-api-version-exception", "get",
                        null, // trigger requires api-version properties – see FluxOpenApiApiVersionContractTests
                        400));

        map.put("notAcceptableApiVersionException",
                new FluxOperationFixture("api-version",
                        "/not-acceptable-api-version", "get",
                        null, // trigger requires api-version properties so FluxApiVersionController is registered
                        400));

        return map;
    }

    /**
     * Immutable descriptor for a single documented WebFlux operation.
     *
     * @param scenario       configuration scenario that owns this operation
     * @param docPath        path as it appears in the OpenAPI paths section
     * @param docMethod      lowercase HTTP method
     * @param requestBuilder functional interface that executes the trigger; {@code null} when the
     *                       trigger is provided by a dedicated scenario test class
     * @param expectedStatus expected HTTP status code
     */
    public record FluxOperationFixture(
            String scenario,
            String docPath,
            String docMethod,
            Function<WebTestClient, WebTestClient.ResponseSpec> requestBuilder,
            int expectedStatus) {
    }
}
