package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import jakarta.servlet.http.Cookie;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

/**
 * Operation trigger fixtures for the WebMVC OpenAPI strict contract tests.
 * <p>
 * Each entry maps an {@code operationId} (as documented in {@code /v3/api-docs}) to a
 * {@link MvcOperationFixture} that captures:
 * <ul>
 *     <li>The exact HTTP request needed to trigger the documented error response.</li>
 *     <li>The OpenAPI path + method used to look up the documented response example.</li>
 *     <li>The expected HTTP status code.</li>
 * </ul>
 * Fixtures are grouped by scenario so that each scenario test class can filter only its own
 * operations.
 */
public final class MvcOperationFixtures {

    private static final String BASE = "/mvc-extended-problem-detail";

    private MvcOperationFixtures() {
    }

    /**
     * All documented MVC operation fixtures keyed by {@code operationId}.
     */
    public static Map<String, MvcOperationFixture> all() {
        Map<String, MvcOperationFixture> map = new LinkedHashMap<>();

        // ── default scenario ──────────────────────────────────────────────────────────
        map.put("httpRequestMethodNotSupportedException",
                new MvcOperationFixture("default",
                        BASE + "/http-request-method-not-supported-exception", "get",
                        mock -> mock.post().uri(BASE + "/http-request-method-not-supported-exception").exchange(),
                        405));

        map.put("httpMediaTypeNotSupportedException",
                new MvcOperationFixture("default",
                        BASE + "/http-media-type-not-supported-exception", "put",
                        mock -> mock.put().uri(BASE + "/http-media-type-not-supported-exception").exchange(),
                        415));

        map.put("httpMediaTypeNotAcceptableException",
                new MvcOperationFixture("default",
                        BASE + "/http-media-type-not-acceptable-exception", "put",
                        mock -> mock.put().uri(BASE + "/http-media-type-not-acceptable-exception")
                                .header("Accept", "application/xml").exchange(),
                        406));

        map.put("missingPathVariableException",
                new MvcOperationFixture("default",
                        BASE + "/missing-path-variable-exception", "delete",
                        mock -> mock.delete().uri(BASE + "/missing-path-variable-exception").exchange(),
                        500));

        map.put("missingServletRequestParameterException",
                new MvcOperationFixture("default",
                        BASE + "/missing-servlet-request-parameter-exception", "get",
                        mock -> mock.get().uri(BASE + "/missing-servlet-request-parameter-exception").exchange(),
                        400));

        map.put("missingServletRequestPartException",
                new MvcOperationFixture("default",
                        BASE + "/missing-servlet-request-part-exception", "put",
                        mock -> mock.put().multipart().contentType(MULTIPART_FORM_DATA)
                                .uri(BASE + "/missing-servlet-request-part-exception").exchange(),
                        400));

        map.put("servletRequestBindingException",
                new MvcOperationFixture("default",
                        BASE + "/servlet-request-binding-exception", "get",
                        mock -> mock.get().uri(BASE + "/servlet-request-binding-exception").exchange(),
                        400));

        map.put("unsatisfiedServletRequestParameterException",
                new MvcOperationFixture("default",
                        BASE + "/unsatisfied-servlet-request-parameter-exception", "get",
                        mock -> mock.get().uri(BASE + "/unsatisfied-servlet-request-parameter-exception")
                                .param("type", "1").exchange(),
                        400));

        map.put("orgSpringWebBindMissingRequestValueException",
                new MvcOperationFixture("default",
                        BASE + "/org-spring-web-bind-missing-request-value-exception", "get",
                        mock -> mock.get().uri(BASE + "/org-spring-web-bind-missing-request-value-exception").exchange(),
                        400));

        map.put("missingMatrixVariableException",
                new MvcOperationFixture("default",
                        BASE + "/missing-matrix-variable-exception/{id}", "get",
                        mock -> mock.get().uri(BASE + "/missing-matrix-variable-exception/abc;list1=a,b,c").exchange(),
                        400));

        map.put("missingRequestCookieException",
                new MvcOperationFixture("default",
                        BASE + "/missing-request-cookie-exception", "get",
                        mock -> mock.get().uri(BASE + "/missing-request-cookie-exception").exchange(),
                        400));

        map.put("missingRequestHeaderException",
                new MvcOperationFixture("default",
                        BASE + "/missing-request-header-exception", "get",
                        mock -> mock.get().uri(BASE + "/missing-request-header-exception").exchange(),
                        400));

        map.put("methodArgumentNotValidException",
                new MvcOperationFixture("default",
                        BASE + "/method-argument-not-valid-exception", "post",
                        mock -> mock.post().uri(BASE + "/method-argument-not-valid-exception")
                                .contentType(APPLICATION_JSON)
                                .content("{\"name\":\"abc\",\"password\":\"123\",\"address\":{\"street\":\"\"}}").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionCookieValue",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-cookie-value", "get",
                        mock -> mock.get().uri(BASE + "/handler-method-validation-exception-cookie-value")
                                .cookie(new Cookie("name", "a")).exchange(),
                        400));

        map.put("handlerMethodValidationExceptionMatrixVariable",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-matrix-variable/{id}", "get",
                        mock -> mock.get()
                                .uri(BASE + "/handler-method-validation-exception-matrix-variable/abc;list=a,b,c")
                                .exchange(),
                        400));

        map.put("handlerMethodValidationExceptionModelAttribute",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-model-attribute", "get",
                        mock -> mock.get().uri(BASE + "/handler-method-validation-exception-model-attribute").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionPathVariable",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-path-variable/{id}", "get",
                        mock -> mock.get()
                                .uri(BASE + "/handler-method-validation-exception-path-variable/a").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestBody",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-body", "post",
                        mock -> mock.post().uri(BASE + "/handler-method-validation-exception-request-body")
                                .contentType(APPLICATION_JSON).content("{\"name\":\"abc\"}").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestBodyValidationResult",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-body-validation-result", "post",
                        mock -> mock.post()
                                .uri(BASE + "/handler-method-validation-exception-request-body-validation-result")
                                .contentType(APPLICATION_JSON).content("[\"\",\"a\"]").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestHeader",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-header", "get",
                        mock -> mock.get().uri(BASE + "/handler-method-validation-exception-request-header")
                                .header("headerValue", "a").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestParam",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-param", "get",
                        mock -> mock.get().uri(BASE + "/handler-method-validation-exception-request-param").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionRequestPart",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-request-part", "get",
                        mock -> mock.get().uri(BASE + "/handler-method-validation-exception-request-part")
                                .header("Content-Type", "multipart/form-data").exchange(),
                        400));

        map.put("handlerMethodValidationExceptionOther",
                new MvcOperationFixture("default",
                        BASE + "/handler-method-validation-exception-other", "get",
                        mock -> mock.get().uri(BASE + "/handler-method-validation-exception-other").exchange(),
                        400));

        map.put("asyncRequestTimeoutException",
                new MvcOperationFixture("default",
                        BASE + "/async-request-timeout-exception", "get",
                        null, // uses async dispatch – tested separately in MvcOpenApiDefaultContractTests
                        503));

        map.put("errorResponseException",
                new MvcOperationFixture("default",
                        BASE + "/error-response-exception", "get",
                        mock -> mock.get().uri(BASE + "/error-response-exception").exchange(),
                        400));

        map.put("extendedErrorResponseException",
                new MvcOperationFixture("default",
                        BASE + "/extended-error-response-exception", "get",
                        mock -> mock.get().uri(BASE + "/extended-error-response-exception").exchange(),
                        500));

        map.put("responseStatusException",
                new MvcOperationFixture("default",
                        BASE + "/response-status-exception", "get",
                        mock -> mock.get().uri(BASE + "/response-status-exception").exchange(),
                        400));

        map.put("serverWebInputException",
                new MvcOperationFixture("default",
                        BASE + "/server-web-input-exception", "get",
                        mock -> mock.get().uri(BASE + "/server-web-input-exception").exchange(),
                        400));

        map.put("unsatisfiedRequestParameterException",
                new MvcOperationFixture("default",
                        BASE + "/unsatisfied-request-parameter-exception", "get",
                        mock -> mock.get().uri(BASE + "/unsatisfied-request-parameter-exception")
                                .param("type", "1").exchange(),
                        400));

        map.put("orgSpringframeworkWebServerMissingRequestValueException",
                new MvcOperationFixture("default",
                        BASE + "/org-springframework-web-server-missing-request-value-exception", "get",
                        mock -> mock.get()
                                .uri(BASE + "/org-springframework-web-server-missing-request-value-exception").exchange(),
                        400));

        map.put("webExchangeBindException",
                new MvcOperationFixture("default",
                        BASE + "/web-exchange-bind-exception", "post",
                        mock -> mock.post().uri(BASE + "/web-exchange-bind-exception")
                                .contentType(APPLICATION_JSON)
                                .content("{\"name\":\"abc\",\"password\":\"123\",\"address\":{\"street\":\"\"}}").exchange(),
                        400));

        map.put("methodNotAllowedException",
                new MvcOperationFixture("default",
                        BASE + "/method-not-allowed-exception", "delete",
                        mock -> mock.delete().uri(BASE + "/method-not-allowed-exception").exchange(),
                        405));

        map.put("notAcceptableStatusException",
                new MvcOperationFixture("default",
                        BASE + "/not-acceptable-status-exception", "get",
                        mock -> mock.get().uri(BASE + "/not-acceptable-status-exception").exchange(),
                        406));

        map.put("contentTooLargeException",
                new MvcOperationFixture("default",
                        BASE + "/content-too-large-exception", "post",
                        mock -> mock.perform(multipart(BASE + "/content-too-large-exception")
                                .file(new MockMultipartFile("file", "test.txt", "text/plain",
                                        "content".getBytes(StandardCharsets.UTF_8)))),
                        413));

        map.put("unsupportedMediaTypeStatusException",
                new MvcOperationFixture("default",
                        BASE + "/unsupported-media-type-status-exception", "post",
                        mock -> mock.post().uri(BASE + "/unsupported-media-type-status-exception").exchange(),
                        415));

        map.put("serverErrorException",
                new MvcOperationFixture("default",
                        BASE + "/server-error-exception", "get",
                        mock -> mock.get().uri(BASE + "/server-error-exception").exchange(),
                        500));

        map.put("payloadTooLargeException",
                new MvcOperationFixture("default",
                        BASE + "/payload-too-large-exception", "post",
                        mock -> mock.perform(multipart(BASE + "/payload-too-large-exception")
                                .file(new MockMultipartFile("file", "test.txt", "text/plain",
                                        "content".getBytes(StandardCharsets.UTF_8)))),
                        413));

        map.put("conversionNotSupportedException",
                new MvcOperationFixture("default",
                        BASE + "/conversion-not-supported-exception", "get",
                        mock -> mock.get().uri(BASE + "/conversion-not-supported-exception")
                                .param("data", "test-value").exchange(),
                        500));

        map.put("methodArgumentConversionNotSupportedException",
                new MvcOperationFixture("default",
                        BASE + "/method-argument-conversion-not-supported-exception", "get",
                        mock -> mock.get().uri(BASE + "/method-argument-conversion-not-supported-exception")
                                .param("error", "test-value").exchange(),
                        500));

        map.put("typeMismatchException",
                new MvcOperationFixture("default",
                        BASE + "/type-mismatch-exception", "get",
                        mock -> mock.get().uri(BASE + "/type-mismatch-exception").exchange(),
                        400));

        map.put("methodArgumentTypeMismatchException",
                new MvcOperationFixture("default",
                        BASE + "/method-argument-type-mismatch-exception", "get",
                        mock -> mock.get().uri(BASE + "/method-argument-type-mismatch-exception")
                                .param("integer", "a").exchange(),
                        400));

        map.put("httpMessageNotReadableException",
                new MvcOperationFixture("default",
                        BASE + "/http-message-not-readable-exception", "post",
                        mock -> mock.post().uri(BASE + "/http-message-not-readable-exception")
                                .contentType(APPLICATION_JSON).content("{").exchange(),
                        400));

        map.put("httpMessageNotWritableException",
                new MvcOperationFixture("default",
                        BASE + "/http-message-not-writable-exception", "get",
                        mock -> mock.get().uri(BASE + "/http-message-not-writable-exception").exchange(),
                        500));

        map.put("methodValidationException",
                new MvcOperationFixture("default",
                        BASE + "/method-validation-exception", "get",
                        mock -> mock.get().uri(BASE + "/method-validation-exception").exchange(),
                        500));

        map.put("noResourceFoundException",
                new MvcOperationFixture("default",
                        BASE + "/no-resource-found-exception", "get",
                        mock -> mock.get().uri(BASE + "/no-resource-found-exception").exchange(),
                        404));

        // ── random-port scenario ─────────────────────────────────────────────────────
        // asyncRequestNotUsableException is tested via a real HTTP connection with tiny
        // timeout; it cannot be triggered reliably via MockMvc and is therefore only
        // documented in the OpenAPI spec with an informational note. The contract test
        // for this operation lives in MvcOpenApiRandomPortContractTests.
        map.put("asyncRequestNotUsableException",
                new MvcOperationFixture("random-port",
                        BASE + "/async-request-not-usable-exception", "get",
                        null, // trigger provided by MvcOpenApiRandomPortContractTests
                        500));

        // ── multipart-limit scenario ──────────────────────────────────────────────────
        map.put("maxUploadSizeExceededException",
                new MvcOperationFixture("multipart-limit",
                        BASE + "/max-upload-size-exceeded-exception", "post",
                        null, // trigger provided by MvcOpenApiMultipartContractTests
                        413));

        // ── api-version scenario ──────────────────────────────────────────────────────
        map.put("invalidApiVersionException",
                new MvcOperationFixture("api-version",
                        BASE + "/invalid-api-version-exception", "get",
                        null, // trigger requires api-version properties – see MvcOpenApiApiVersionContractTests
                        400));

        map.put("missingApiVersionException",
                new MvcOperationFixture("api-version",
                        BASE + "/missing-api-version-exception", "get",
                        null, // trigger requires api-version properties – see MvcOpenApiApiVersionContractTests
                        400));

        map.put("notAcceptableApiVersionException",
                new MvcOperationFixture("api-version",
                        "/not-acceptable-api-version", "get",
                        null, // trigger requires api-version properties so MvcApiVersionController is registered
                        400));

        // ── no-handler-found scenario ─────────────────────────────────────────────────
        map.put("noHandlerFoundException",
                new MvcOperationFixture("no-handler-found",
                        BASE + "/no-handler-found-exception", "get",
                        null, // trigger requires spring.web.resources.add-mappings=false – see MvcOpenApiNoHandlerFoundContractTests
                        404));

        // ── actuator-endpoint scenario ────────────────────────────────────────────────
        map.put("invalidEndpointBadRequestException",
                new MvcOperationFixture("actuator-endpoint",
                        "/actuator/demo/{name}", "get",
                        null, // trigger requires management.endpoints.web.exposure.include=demo – see MvcOpenApiActuatorContractTests
                        400));

        return map;
    }

    /**
     * Immutable descriptor for a single documented MVC operation.
     *
     * @param scenario       configuration scenario that owns this operation
     * @param docPath        path as it appears in the OpenAPI paths section (may contain
     *                       {@code {id}} template variables)
     * @param docMethod      lowercase HTTP method used in the OpenAPI paths section
     * @param requestBuilder functional interface that executes the trigger request against a
     *                       {@link MockMvcTester}; {@code null} when the trigger is provided
     *                       by a dedicated scenario test class
     * @param expectedStatus expected HTTP status code
     */
    public record MvcOperationFixture(
            String scenario,
            String docPath,
            String docMethod,
            Function<MockMvcTester, MvcTestResult> requestBuilder,
            int expectedStatus) {
    }
}
