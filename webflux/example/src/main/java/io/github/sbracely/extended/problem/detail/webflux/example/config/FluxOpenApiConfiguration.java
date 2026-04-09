package io.github.sbracely.extended.problem.detail.webflux.example.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class FluxOpenApiConfiguration {

    private static final String PROBLEM_JSON = "application/problem+json";
    private static final String ERROR_SCHEMA_REF = "#/components/schemas/Error";
    private static final String EXTENDED_PROBLEM_DETAIL_SCHEMA_REF = "#/components/schemas/ExtendedProblemDetail";

    @Bean
    OpenAPI webFluxExampleOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Extended Problem Detail WebFlux Example API")
                        .description("Example Spring WebFlux endpoints that demonstrate Extended Problem Detail responses.")
                        .version("1.1.0"))
                .servers(List.of(new Server()
                        .url("/")
                        .description("Relative server URL")))
                .components(new Components()
                        .addSchemas("Error", errorSchema())
                        .addSchemas("ExtendedProblemDetail", extendedProblemDetailSchema())
                        .addExamples("ValidationProblemDetailExample", validationProblemDetailExample())
                        .addExamples("GenericBadRequestProblemDetailExample", genericBadRequestProblemDetailExample())
                        .addExamples("MethodNotAllowedProblemDetailExample", methodNotAllowedProblemDetailExample())
                        .addExamples("NotAcceptableProblemDetailExample", notAcceptableProblemDetailExample())
                        .addExamples("UnsupportedMediaTypeProblemDetailExample", unsupportedMediaTypeProblemDetailExample())
                        .addExamples("ServerProblemDetailExample", serverProblemDetailExample())
                        .addExamples("BusinessProblemDetailExample", businessProblemDetailExample()));
    }

    @Bean
    OpenApiCustomizer webFluxErrorResponseCustomizer() {
        return openApi -> {
            ensureProblemSchemas(openApi);
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();
                responses.remove("200");
                responses.remove("201");
                responses.remove("202");
                responses.remove("204");
                responses.remove("default");
                FluxErrorResponseSpec errorResponseSpec = responseSpec(operation.getOperationId());
                responses.addApiResponse(errorResponseSpec.statusCode(),
                        response(errorResponseSpec.description(), errorResponseSpec.example()));
                operation.setDescription(testGuidance(errorResponseSpec.trigger(),
                        "src/test/java/io/github/sbracely/extended/problem/detail/webflux/example/controller/FluxControllerTests.java"));
            }));
        };
    }

    private static void ensureProblemSchemas(OpenAPI openApi) {
        Components components = openApi.getComponents();
        if (components == null) {
            components = new Components();
            openApi.setComponents(components);
        }
        components.addSchemas("Error", errorSchema());
        components.addSchemas("ExtendedProblemDetail", extendedProblemDetailSchema());
    }

    private static Schema<?> errorSchema() {
        return new ObjectSchema()
                .description("Detailed error entry inside the Extended Problem Detail response.")
                .addProperty("type", errorTypeSchema())
                .addProperty("target", new StringSchema()
                        .description("Field, parameter, cookie, header, or business target associated with the error."))
                .addProperty("message", new StringSchema()
                        .description("Human-readable explanation of the error."));
    }

    private static StringSchema errorTypeSchema() {
        StringSchema schema = new StringSchema();
        schema.description("Source of the error.");
        schema.addEnumItemObject("PARAMETER");
        schema.addEnumItemObject("COOKIE");
        schema.addEnumItemObject("HEADER");
        schema.addEnumItemObject("BUSINESS");
        return schema;
    }

    private static Schema<?> extendedProblemDetailSchema() {
        return new ObjectSchema()
                .description("RFC 9457 problem detail extended with an errors array.")
                .addProperty("type", new StringSchema()
                        .format("uri")
                        .description("Problem type URI."))
                .addProperty("title", new StringSchema()
                        .description("Short summary of the problem."))
                .addProperty("status", new IntegerSchema()
                        .description("HTTP status code."))
                .addProperty("detail", new StringSchema()
                        .description("Human-readable explanation for this occurrence."))
                .addProperty("instance", new StringSchema()
                        .format("uri")
                        .description("URI identifying the specific occurrence."))
                .addProperty("errors", errorListSchema());
    }

    private static ArraySchema errorListSchema() {
        ArraySchema schema = new ArraySchema();
        schema.description("Detailed validation or business errors.");
        Schema<Object> itemSchema = new Schema<>();
        itemSchema.$ref(ERROR_SCHEMA_REF);
        schema.items(itemSchema);
        return schema;
    }

    private static ApiResponse response(String description, Example example) {
        return new ApiResponse()
                .description(description + ". See the WebFlux example tests for concrete triggering inputs.")
                .content(problemDetailContent(example));
    }

    private static Content problemDetailContent(Example example) {
        return new Content().addMediaType(PROBLEM_JSON, new MediaType()
                .schema(new Schema<>().$ref(EXTENDED_PROBLEM_DETAIL_SCHEMA_REF))
                .addExamples("example", example));
    }

    private static Example validationProblemDetailExample() {
        return problemExample("Validation error", "Bad Request", 400, "Invalid request content.",
                "/flux-extended-problem-detail/web-exchange-bind-exception",
                List.of(
                        error("PARAMETER", "name", "Name length must be between 6-10"),
                        error("PARAMETER", "age", "Age cannot be null"),
                        error("PARAMETER", "password", "Password and confirm password do not match"),
                        error("PARAMETER", "confirmPassword", "Password and confirm password do not match")));
    }

    private static Example genericBadRequestProblemDetailExample() {
        return problemExample("Bad request", "Bad Request", 400,
                "Required query parameter 'id' is not present.",
                "/flux-extended-problem-detail/missing-request-value-exception");
    }

    private static Example methodNotAllowedProblemDetailExample() {
        return problemExample("Method not allowed", "Method Not Allowed", 405,
                "Supported methods: [GET]",
                "/flux-extended-problem-detail/method-not-allowed-exception");
    }

    private static Example notAcceptableProblemDetailExample() {
        return problemExample("Not acceptable", "Not Acceptable", 406,
                "Acceptable representations: [application/json].",
                "/flux-extended-problem-detail/not-acceptable-status-exception");
    }

    private static Example unsupportedMediaTypeProblemDetailExample() {
        return problemExample("Unsupported media type", "Unsupported Media Type", 415, null,
                "/flux-extended-problem-detail/unsupported-media-type-status-exception");
    }

    private static Example serverProblemDetailExample() {
        return problemExample("Server error", "Internal Server Error", 500,
                "server error",
                "/flux-extended-problem-detail/server-error-exception");
    }

    private static Example businessProblemDetailExample() {
        return problemExample("Business error", "Payment failed title", 500, "Payment failed details",
                "/flux-extended-problem-detail/extended-error-response-exception",
                List.of(
                        error("BUSINESS", null, "Insufficient balance"),
                        error("BUSINESS", null, "Payment frequent")));
    }

    private static FluxErrorResponseSpec responseSpec(String operationId) {
        return switch (operationId) {
            case "methodNotAllowedException" ->
                    new FluxErrorResponseSpec("405", "405 method not allowed error", methodNotAllowedProblemDetailExample(),
                            "DELETE /flux-extended-problem-detail/method-not-allowed-exception");
            case "notAcceptableStatusException" ->
                    new FluxErrorResponseSpec("406", "406 not acceptable error", notAcceptableProblemDetailExample(),
                            "GET /flux-extended-problem-detail/not-acceptable-status-exception");
            case "unsupportedMediaTypeStatusException" ->
                    new FluxErrorResponseSpec("415", "415 unsupported media type error", unsupportedMediaTypeProblemDetailExample(),
                            "POST /flux-extended-problem-detail/unsupported-media-type-status-exception without Content-Type: application/xml");
            case "extendedErrorResponseException" ->
                    new FluxErrorResponseSpec("500", "500 business error", businessProblemDetailExample(),
                            "GET /flux-extended-problem-detail/extended-error-response-exception");
            case "errorResponseException" ->
                    new FluxErrorResponseSpec("400", "400 error response exception",
                            problemExample("Error response", "Error title", 400, "Error details",
                                    "/flux-extended-problem-detail/error-response-exception",
                                    List.of(
                                            error("BUSINESS", null, "Error message 1"),
                                            error("BUSINESS", null, "Error message 2"))),
                            "GET /flux-extended-problem-detail/error-response-exception");
            case "responseStatusException" ->
                    new FluxErrorResponseSpec("400", "400 response status error",
                            problemExample("Response status error", "Bad Request", 400, "exception",
                                    "/flux-extended-problem-detail/response-status-exception"),
                            "GET /flux-extended-problem-detail/response-status-exception");
            case "missingRequestValueException" ->
                    new FluxErrorResponseSpec("400", "400 missing request value error", genericBadRequestProblemDetailExample(),
                            "GET /flux-extended-problem-detail/missing-request-value-exception without id");
            case "unsatisfiedRequestParameterException" ->
                    new FluxErrorResponseSpec("400", "400 invalid request parameters error",
                            problemExample("Invalid request parameters", "Bad Request", 400,
                                    "Invalid request parameters.",
                                    "/flux-extended-problem-detail/unsatisfied-request-parameter-exception"),
                            "GET /flux-extended-problem-detail/unsatisfied-request-parameter-exception");
            case "contentTooLargeException" ->
                    new FluxErrorResponseSpec("413", "413 content too large error",
                            problemExample("Content too large", "Content Too Large", 413, null,
                                    "/flux-extended-problem-detail/content-too-large-exception"),
                            "POST /flux-extended-problem-detail/content-too-large-exception with a 1MB request body");
            case "payloadTooLargeException" ->
                    new FluxErrorResponseSpec("413", "413 content too large error",
                            problemExample("Content too large", "Content Too Large", 413, null,
                                    "/flux-extended-problem-detail/payload-too-large-exception"),
                            "POST /flux-extended-problem-detail/payload-too-large-exception with body text");
            case "serverWebInputException" ->
                    new FluxErrorResponseSpec("400", "400 server web input error",
                            problemExample("Server web input error", "Bad Request", 400, "server web input error",
                                    "/flux-extended-problem-detail/server-web-input-exception"),
                            "GET /flux-extended-problem-detail/server-web-input-exception");
            case "serverErrorException" ->
                    new FluxErrorResponseSpec("500", "500 server error", serverProblemDetailExample(),
                            "GET /flux-extended-problem-detail/server-error-exception");
            case "invalidApiVersionException" ->
                    new FluxErrorResponseSpec("400", "400 invalid API version error",
                            problemExample("Invalid API version", "Bad Request", 400, "Invalid API version: '3.0.0'.",
                                    "/flux-extended-problem-detail/invalid-api-version-exception"),
                            "GET /flux-extended-problem-detail/invalid-api-version-exception with header API-Version: 3");
            case "missingApiVersionException" ->
                    new FluxErrorResponseSpec("400", "400 missing API version error",
                            problemExample("Missing API version", "Bad Request", 400, "API version is required.",
                                    "/flux-extended-problem-detail/missing-api-version-exception"),
                            "GET /flux-extended-problem-detail/missing-api-version-exception without API-Version header");
            case "webExchangeBindException", "handlerMethodValidationExceptionCookieValue",
                 "handlerMethodValidationExceptionMatrix", "handlerMethodValidationExceptionModelAttribute",
                 "handlerMethodValidationExceptionPathVariable", "handlerMethodValidationExceptionRequestBody",
                  "handlerMethodValidationExceptionRequestBodyValidationResult", "handlerMethodValidationExceptionRequestHeader",
                  "handlerMethodValidationExceptionRequestParam", "handlerMethodValidationExceptionRequestPart",
                  "handlerMethodValidationExceptionOther", "methodValidationException" ->
                    validationResponseSpec(operationId);
            default -> new FluxErrorResponseSpec("400", "400 bad request error", genericBadRequestProblemDetailExample(),
                    "See the referenced WebFlux test for the exact trigger");
        };
    }

    private static FluxErrorResponseSpec validationResponseSpec(String operationId) {
        return switch (operationId) {
            case "webExchangeBindException" ->
                    new FluxErrorResponseSpec("400", "400 validation error", validationProblemDetailExample(),
                            "POST /flux-extended-problem-detail/web-exchange-bind-exception with application/json body {\"name\":\"abc\",\"password\":\"123\"}");
            case "handlerMethodValidationExceptionCookieValue" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-cookie-value",
                                    List.of(error("COOKIE", "cookieValue", "cookie cannot be empty"))),
                            "GET /flux-extended-problem-detail/handler-method-validation-exception-cookie-value with cookie cookieValue=");
            case "handlerMethodValidationExceptionMatrix" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-matrix/abc;list=a,b,c",
                                    List.of(error("PARAMETER", "list", "list maximum size is 2"))),
                            "GET /flux-extended-problem-detail/handler-method-validation-exception-matrix/abc;list=a,b,c");
            case "handlerMethodValidationExceptionModelAttribute" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-model-attribute",
                                    List.of(error("PARAMETER", "password", "Password cannot be empty"))),
                            "GET /flux-extended-problem-detail/handler-method-validation-exception-model-attribute");
            case "handlerMethodValidationExceptionPathVariable" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-path-variable/abc",
                                    List.of(error("PARAMETER", "id", "id length must be at least 5"))),
                            "GET /flux-extended-problem-detail/handler-method-validation-exception-path-variable/abc");
            case "handlerMethodValidationExceptionRequestBody" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-request-body",
                                    List.of(error("PARAMETER", "password", "Password cannot be empty"))),
                            "POST /flux-extended-problem-detail/handler-method-validation-exception-request-body with application/json body {\"name\":\"abc\"}");
            case "handlerMethodValidationExceptionRequestBodyValidationResult" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-request-body-validation-result",
                                    List.of(error("PARAMETER", null, "Element cannot contain empty values"))),
                            "POST /flux-extended-problem-detail/handler-method-validation-exception-request-body-validation-result with application/json body [\"\",\"a\"]");
            case "handlerMethodValidationExceptionRequestHeader" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-request-header",
                                    List.of(error("HEADER", "headerValue", "Header cannot be empty"))),
                            "GET /flux-extended-problem-detail/handler-method-validation-exception-request-header with header headerValue=");
            case "handlerMethodValidationExceptionRequestParam" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-request-param",
                                    List.of(
                                            error("PARAMETER", "param", "Parameter cannot be empty"),
                                            error("PARAMETER", "value", "Length must be at least 5"))),
                            "GET /flux-extended-problem-detail/handler-method-validation-exception-request-param?param=&value=ab");
            case "handlerMethodValidationExceptionRequestPart" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-request-part",
                                    List.of(error("PARAMETER", "file", "File cannot be empty"))),
                            "POST /flux-extended-problem-detail/handler-method-validation-exception-request-part with an empty body");
            case "handlerMethodValidationExceptionOther" ->
                    new FluxErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/flux-extended-problem-detail/handler-method-validation-exception-other", null),
                            "GET /flux-extended-problem-detail/handler-method-validation-exception-other");
            case "methodValidationException" ->
                    new FluxErrorResponseSpec("500", "500 validation error",
                            problemExample("Validation failed", "Internal Server Error", 500, "Validation failed",
                                    "/flux-extended-problem-detail/method-validation-exception",
                                    List.of(
                                            error("PARAMETER", "name", "name must not be blank"),
                                            error("PARAMETER", "name", "name must not be null"),
                                            error("PARAMETER", "password", "Password and confirm password do not match"),
                                            error("PARAMETER", "name", "Name cannot be blank"),
                                            error("PARAMETER", "age", "Age cannot be null"),
                                            error("PARAMETER", "confirmPassword", "Password and confirm password do not match"),
                                            error("PARAMETER", "name", "Name length must be between 6-10"),
                                            error("PARAMETER", null, "Name is not valid"))),
                            "GET /flux-extended-problem-detail/method-validation-exception");
            default ->
                    new FluxErrorResponseSpec("400", "400 validation error", validationProblemDetailExample(),
                            "See the referenced WebFlux test for the exact validation trigger");
        };
    }

    private static Example validationFailureExample(String instance, List<Map<String, Object>> errors) {
        return problemExample("Validation error", "Bad Request", 400, "Validation failure", instance, errors);
    }

    private static Example problemExample(String summary, String title, int status, String detail, String instance) {
        return problemExample(summary, title, status, detail, instance, null);
    }

    private static Example problemExample(String summary, String title, int status, String detail, String instance,
                                          List<Map<String, Object>> errors) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", title);
        payload.put("status", status);
        if (detail != null) {
            payload.put("detail", detail);
        }
        payload.put("instance", instance);
        if (errors != null) {
            payload.put("errors", errors);
        }
        return new Example().summary(summary).value(payload);
    }

    private static Map<String, Object> error(String type, String target, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", type);
        if (target != null) {
            payload.put("target", target);
        }
        payload.put("message", message);
        return payload;
    }

    private static String testGuidance(String trigger, String testPath) {
        return "Real trigger from tests: " + trigger + ". "
                + "For the exact request setup and assertions, see " + testPath + ".";
    }

    private record FluxErrorResponseSpec(String statusCode, String description, Example example, String trigger) {
    }
}
