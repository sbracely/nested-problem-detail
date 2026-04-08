package io.github.sbracely.extended.problem.detail.webmvc.example.config;

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
public class Boot3MvcOpenApiConfiguration {

    private static final String PROBLEM_JSON = "application/problem+json";
    private static final String ERROR_SCHEMA_REF = "#/components/schemas/Error";
    private static final String EXTENDED_PROBLEM_DETAIL_SCHEMA_REF = "#/components/schemas/ExtendedProblemDetail";

    @Bean
    OpenAPI webMvcExampleOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Extended Problem Detail WebMVC Example API")
                        .description("Example Spring WebMVC endpoints that demonstrate Extended Problem Detail responses.")
                        .version("1.0.1"))
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
    OpenApiCustomizer webMvcErrorResponseCustomizer() {
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
                Boot3MvcErrorResponseSpec errorResponseSpec = responseSpec(operation.getOperationId());
                responses.addApiResponse(errorResponseSpec.statusCode(),
                        response(errorResponseSpec.description(), errorResponseSpec.example()));
                operation.setDescription(testGuidance(errorResponseSpec.trigger(),
                        testPath(operation.getOperationId())));
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
                .description(description + ". See the MVC example tests for concrete triggering inputs.")
                .content(problemDetailContent(example));
    }

    private static Content problemDetailContent(Example example) {
        return new Content().addMediaType(PROBLEM_JSON, new MediaType()
                .schema(new Schema<>().$ref(EXTENDED_PROBLEM_DETAIL_SCHEMA_REF))
                .addExamples("example", example));
    }

    private static Example validationProblemDetailExample() {
        return problemExample("Validation error", "Bad Request", 400, "Invalid request content.",
                "/mvc-extended-problem-detail/method-argument-not-valid-exception",
                List.of(
                        error("PARAMETER", "name", "Name length must be between 6-10"),
                        error("PARAMETER", "age", "Age cannot be null"),
                        error("PARAMETER", "password", "Password and confirm password do not match"),
                        error("PARAMETER", "confirmPassword", "Password and confirm password do not match")));
    }

    private static Example genericBadRequestProblemDetailExample() {
        return problemExample("Bad request", "Bad Request", 400,
                "Required parameter 'id' is not present.",
                "/mvc-extended-problem-detail/missing-servlet-request-parameter-exception");
    }

    private static Example methodNotAllowedProblemDetailExample() {
        return problemExample("Method not allowed", "Method Not Allowed", 405,
                "Method 'POST' is not supported.",
                "/mvc-extended-problem-detail/http-request-method-not-supported-exception");
    }

    private static Example notAcceptableProblemDetailExample() {
        return problemExample("Not acceptable", "Not Acceptable", 406,
                "Acceptable representations: [application/json].",
                "/mvc-extended-problem-detail/http-media-type-not-acceptable-exception");
    }

    private static Example unsupportedMediaTypeProblemDetailExample() {
        return problemExample("Unsupported media type", "Unsupported Media Type", 415,
                "Content-Type 'null' is not supported.",
                "/mvc-extended-problem-detail/http-media-type-not-supported-exception");
    }

    private static Example serverProblemDetailExample() {
        return problemExample("Server error", "Internal Server Error", 500,
                "server error",
                "/mvc-extended-problem-detail/server-error-exception");
    }

    private static Example businessProblemDetailExample() {
        return problemExample("Business error", "Internal Server Error", 500, "Payment failed",
                "/mvc-extended-problem-detail/extended-error-response-exception",
                List.of(
                        error("BUSINESS", null, "Insufficient balance"),
                        error("BUSINESS", null, "Payment frequent")));
    }

    private static Boot3MvcErrorResponseSpec responseSpec(String operationId) {
        return switch (operationId) {
            case "methodNotAllowedException" ->
                    new Boot3MvcErrorResponseSpec("405", "405 method not allowed error",
                            problemExample("Method not allowed", "Method Not Allowed", 405,
                                    "Supported methods: [GET, POST]",
                                    "/mvc-extended-problem-detail/method-not-allowed-exception"),
                            "DELETE /mvc-extended-problem-detail/method-not-allowed-exception");
            case "httpRequestMethodNotSupportedException" ->
                    new Boot3MvcErrorResponseSpec("405", "405 method not allowed error", methodNotAllowedProblemDetailExample(),
                            "POST /mvc-extended-problem-detail/http-request-method-not-supported-exception");
            case "httpMediaTypeNotAcceptableException" ->
                    new Boot3MvcErrorResponseSpec("406", "406 not acceptable error",
                            notAcceptableProblemDetailExample(),
                            "PUT /mvc-extended-problem-detail/http-media-type-not-acceptable-exception with Accept: application/xml");
            case "notAcceptableStatusException" ->
                    new Boot3MvcErrorResponseSpec("406", "406 not acceptable error",
                            problemExample("Not acceptable", "Not Acceptable", 406,
                                    "Acceptable representations: [application/json].",
                                    "/mvc-extended-problem-detail/not-acceptable-status-exception"),
                            "GET /mvc-extended-problem-detail/not-acceptable-status-exception");
            case "httpMediaTypeNotSupportedException" ->
                    new Boot3MvcErrorResponseSpec("415", "415 unsupported media type error",
                            unsupportedMediaTypeProblemDetailExample(),
                            "PUT /mvc-extended-problem-detail/http-media-type-not-supported-exception");
            case "unsupportedMediaTypeStatusException" ->
                    new Boot3MvcErrorResponseSpec("415", "415 unsupported media type error",
                            problemExample("Unsupported media type", "Unsupported Media Type", 415,
                                    "Could not parse Content-Type.",
                                    "/mvc-extended-problem-detail/unsupported-media-type-status-exception"),
                            "POST /mvc-extended-problem-detail/unsupported-media-type-status-exception");
            case "extendedErrorResponseException" ->
                    new Boot3MvcErrorResponseSpec("500", "500 business error", businessProblemDetailExample(),
                            "GET /mvc-extended-problem-detail/extended-error-response-exception");
            case "errorResponseException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 error response exception",
                            problemExample("Error response", "Bad Request", 400, null,
                                    "/mvc-extended-problem-detail/error-response-exception"),
                            "GET /mvc-extended-problem-detail/error-response-exception");
            case "responseStatusException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 response status error",
                            problemExample("Response status error", "Bad Request", 400, "exception",
                                    "/mvc-extended-problem-detail/response-status-exception"),
                            "GET /mvc-extended-problem-detail/response-status-exception");
            case "serverWebInputException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 server web input error",
                            problemExample("Server web input error", "Bad Request", 400, "server web input error",
                                    "/mvc-extended-problem-detail/server-web-input-exception"),
                            "GET /mvc-extended-problem-detail/server-web-input-exception");
            case "missingServletRequestPartException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 missing request part error",
                            problemExample("Missing request part", "Bad Request", 400,
                                    "Required part 'file' is not present.",
                                    "/mvc-extended-problem-detail/missing-servlet-request-part-exception"),
                            "PUT multipart/form-data /mvc-extended-problem-detail/missing-servlet-request-part-exception without file part");
            case "servletRequestBindingException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 servlet request binding error",
                            problemExample("Servlet request binding error", "Bad Request", 400, null,
                                    "/mvc-extended-problem-detail/servlet-request-binding-exception"),
                            "GET /mvc-extended-problem-detail/servlet-request-binding-exception");
            case "unsatisfiedServletRequestParameterException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 invalid request parameters error",
                            problemExample("Invalid request parameters", "Bad Request", 400,
                                    "Invalid request parameters.",
                                    "/mvc-extended-problem-detail/unsatisfied-servlet-request-parameter-exception"),
                            "GET /mvc-extended-problem-detail/unsatisfied-servlet-request-parameter-exception?type=1");
            case "unsatisfiedRequestParameterException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 invalid request parameters error",
                            problemExample("Invalid request parameters", "Bad Request", 400,
                                    "Invalid request parameters.",
                                    "/mvc-extended-problem-detail/unsatisfied-request-parameter-exception"),
                            "GET /mvc-extended-problem-detail/unsatisfied-request-parameter-exception?type=1");
            case "missingRequestHeaderException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 missing request header error",
                            problemExample("Missing request header", "Bad Request", 400,
                                    "Required header 'header' is not present.",
                                    "/mvc-extended-problem-detail/missing-request-header-exception"),
                            "GET /mvc-extended-problem-detail/missing-request-header-exception without header");
            case "missingRequestCookieException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 missing request cookie error",
                            problemExample("Missing request cookie", "Bad Request", 400,
                                    "Required cookie 'cookieValue' is not present.",
                                    "/mvc-extended-problem-detail/missing-request-cookie-exception"),
                            "GET /mvc-extended-problem-detail/missing-request-cookie-exception without cookieValue cookie");
            case "missingMatrixVariableException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 missing matrix variable error",
                            problemExample("Missing matrix variable", "Bad Request", 400,
                                    "Required path parameter 'list' is not present.",
                                    "/mvc-extended-problem-detail/missing-matrix-variable-exception/abc;list1=a,b,c"),
                            "GET /mvc-extended-problem-detail/missing-matrix-variable-exception/abc;list1=a,b,c");
            case "orgSpringWebBindMissingRequestValueException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 missing request value error",
                            problemExample("Missing request value", "Bad Request", 400, null,
                                    "/mvc-extended-problem-detail/org-spring-web-bind-missing-request-value-exception"),
                            "GET /mvc-extended-problem-detail/org-spring-web-bind-missing-request-value-exception");
            case "orgSpringframeworkWebServerMissingRequestValueException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 missing request param error",
                            problemExample("Missing request param", "Bad Request", 400,
                                    "Required request param 'id' is not present.",
                                    "/mvc-extended-problem-detail/org-springframework-web-server-missing-request-value-exception"),
                            "GET /mvc-extended-problem-detail/org-springframework-web-server-missing-request-value-exception");
            case "missingServletRequestParameterException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 missing request parameter error",
                            genericBadRequestProblemDetailExample(),
                            "GET /mvc-extended-problem-detail/missing-servlet-request-parameter-exception");
            case "payloadTooLargeException" ->
                    new Boot3MvcErrorResponseSpec("413", "413 payload too large error",
                            problemExample("Payload too large", "Content Too Large", 413, "payload too large",
                                    "/mvc-extended-problem-detail/payload-too-large-exception"),
                            "POST multipart/form-data /mvc-extended-problem-detail/payload-too-large-exception with file");
            case "contentTooLargeException" ->
                    new Boot3MvcErrorResponseSpec("413", "413 content too large error",
                            problemExample("Content too large", "Content Too Large", 413, null,
                                    "/mvc-extended-problem-detail/content-too-large-exception"),
                            "POST multipart/form-data /mvc-extended-problem-detail/content-too-large-exception with file");
            case "maxUploadSizeExceededException" ->
                    new Boot3MvcErrorResponseSpec("413", "413 max upload size exceeded error",
                            problemExample("Maximum upload size exceeded", "Content Too Large", 413,
                                    "Maximum upload size exceeded",
                                    "/mvc-extended-problem-detail/max-upload-size-exceeded-exception"),
                            "POST multipart/form-data /mvc-extended-problem-detail/max-upload-size-exceeded-exception with a file larger than 1 byte");
            case "asyncRequestTimeoutException" ->
                    new Boot3MvcErrorResponseSpec("503", "503 async timeout error",
                            problemExample("Async timeout", "Service Unavailable", 503, null,
                                    "/mvc-extended-problem-detail/async-request-timeout-exception"),
                            "GET /mvc-extended-problem-detail/async-request-timeout-exception and let async processing time out");
            case "httpMessageNotWritableException" ->
                    new Boot3MvcErrorResponseSpec("500", "500 message not writable error",
                            problemExample("Message not writable", "Internal Server Error", 500,
                                    "Failed to write request",
                                    "/mvc-extended-problem-detail/http-message-not-writable-exception"),
                            "GET /mvc-extended-problem-detail/http-message-not-writable-exception");
            case "asyncRequestNotUsableException" ->
                    new Boot3MvcErrorResponseSpec("500", "500 async request unusable error",
                            problemExample("Async request unusable", "Internal Server Error", 500, null,
                                    "/mvc-extended-problem-detail/async-request-not-usable-exception"),
                            "GET /mvc-extended-problem-detail/async-request-not-usable-exception");
            case "missingPathVariableException" ->
                    new Boot3MvcErrorResponseSpec("500", "500 missing path variable error",
                            problemExample("Missing path variable", "Internal Server Error", 500,
                                    "Required path variable 'id' is not present.",
                                    "/mvc-extended-problem-detail/missing-path-variable-exception"),
                            "DELETE /mvc-extended-problem-detail/missing-path-variable-exception");
            case "serverErrorException" ->
                    new Boot3MvcErrorResponseSpec("500", "500 server error", serverProblemDetailExample(),
                            "GET /mvc-extended-problem-detail/server-error-exception");
            case "conversionNotSupportedException" ->
                    new Boot3MvcErrorResponseSpec("500", "500 conversion not supported error",
                            problemExample("Conversion not supported", "Internal Server Error", 500,
                                    "Failed to convert value of type 'java.lang.String' to required type 'io.github.sbracely.extended.problem.detail.webmvc.example.request.Boot3MvcProblemDetailRequest'.",
                                    "/mvc-extended-problem-detail/conversion-not-supported-exception"),
                            "GET /mvc-extended-problem-detail/conversion-not-supported-exception?data=test-value");
            case "methodArgumentConversionNotSupportedException" ->
                    new Boot3MvcErrorResponseSpec("500", "500 method argument conversion not supported error",
                            problemExample("Method argument conversion not supported", "Internal Server Error", 500,
                                    "Failed to convert value of type 'java.lang.String' to required type 'io.github.sbracely.extended.problem.detail.webmvc.example.request.Boot3MvcProblemDetailRequest'.",
                                    "/mvc-extended-problem-detail/method-argument-conversion-not-supported-exception"),
                            "GET /mvc-extended-problem-detail/method-argument-conversion-not-supported-exception?error=test-value");
            case "typeMismatchException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 type mismatch error",
                            problemExample("Type mismatch", "Bad Request", 400,
                                    "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'.",
                                    "/mvc-extended-problem-detail/type-mismatch-exception"),
                            "GET /mvc-extended-problem-detail/type-mismatch-exception?integer=a");
            case "methodArgumentTypeMismatchException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 method argument type mismatch error",
                            problemExample("Method argument type mismatch", "Bad Request", 400,
                                    "Failed to convert value 'a' to required type 'java.lang.Integer'.",
                                    "/mvc-extended-problem-detail/method-argument-type-mismatch-exception"),
                            "GET /mvc-extended-problem-detail/method-argument-type-mismatch-exception?integer=a");
            case "httpMessageNotReadableException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 message not readable error",
                            problemExample("Message not readable", "Bad Request", 400,
                                    "Failed to read request",
                                    "/mvc-extended-problem-detail/http-message-not-readable-exception"),
                            "POST /mvc-extended-problem-detail/http-message-not-readable-exception with malformed application/json body");
            case "invalidApiVersionException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 invalid API version error",
                            problemExample("Invalid API version", "Bad Request", 400,
                                    "Invalid API version: '3.0.0'.",
                                    "/mvc-extended-problem-detail/invalid-api-version-exception"),
                            "GET /mvc-extended-problem-detail/invalid-api-version-exception with header API-Version: 3");
            case "missingApiVersionException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 missing API version error",
                            problemExample("Missing API version", "Bad Request", 400,
                                    "API version is required.",
                                    "/mvc-extended-problem-detail/missing-api-version-exception"),
                            "GET /mvc-extended-problem-detail/missing-api-version-exception without API-Version header");
            case "methodArgumentNotValidException", "handlerMethodValidationExceptionCookieValue",
                   "handlerMethodValidationExceptionMatrixVariable", "handlerMethodValidationExceptionModelAttribute",
                   "handlerMethodValidationExceptionPathVariable", "handlerMethodValidationExceptionRequestBody",
                   "handlerMethodValidationExceptionRequestBodyValidationResult", "handlerMethodValidationExceptionRequestHeader",
                   "handlerMethodValidationExceptionRequestParam", "handlerMethodValidationExceptionRequestPart",
                  "handlerMethodValidationExceptionOther", "webExchangeBindException", "methodValidationException" ->
                    validationResponseSpec(operationId);
            default -> new Boot3MvcErrorResponseSpec("400", "400 bad request error", genericBadRequestProblemDetailExample(),
                    "See the referenced MVC test for the exact trigger");
        };
    }

    private static Boot3MvcErrorResponseSpec validationResponseSpec(String operationId) {
        return switch (operationId) {
            case "methodArgumentNotValidException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error", validationProblemDetailExample(),
                            "POST /mvc-extended-problem-detail/method-argument-not-valid-exception with application/json body {\"name\":\"abc\",\"password\":\"123\"}");
            case "webExchangeBindException" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            problemExample("Validation error", "Bad Request", 400, "Invalid request content.",
                                    "/mvc-extended-problem-detail/web-exchange-bind-exception",
                                    List.of(
                                            error("PARAMETER", "name", "Name length must be between 6-10"),
                                            error("PARAMETER", "age", "Age cannot be null"),
                                            error("PARAMETER", "password", "Password and confirm password do not match"),
                                            error("PARAMETER", "confirmPassword", "Password and confirm password do not match"))),
                            "POST /mvc-extended-problem-detail/web-exchange-bind-exception with application/json body {\"name\":\"abc\",\"password\":\"123\"}");
            case "handlerMethodValidationExceptionCookieValue" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-cookie-value",
                                    List.of(error("COOKIE", "name", "Name length must be at least 2"))),
                            "GET /mvc-extended-problem-detail/handler-method-validation-exception-cookie-value with cookie name=a");
            case "handlerMethodValidationExceptionMatrixVariable" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-matrix-variable/abc;list=a,b,c",
                                    List.of(error("PARAMETER", "list", "Maximum size is 2"))),
                            "GET /mvc-extended-problem-detail/handler-method-validation-exception-matrix-variable/abc;list=a,b,c");
            case "handlerMethodValidationExceptionModelAttribute" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-model-attribute",
                                    List.of(error("PARAMETER", "password", "Password cannot be empty"))),
                            "GET /mvc-extended-problem-detail/handler-method-validation-exception-model-attribute");
            case "handlerMethodValidationExceptionPathVariable" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-path-variable/a",
                                    List.of(error("PARAMETER", "id", "ID minimum length is 2"))),
                            "GET /mvc-extended-problem-detail/handler-method-validation-exception-path-variable/a");
            case "handlerMethodValidationExceptionRequestBody" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-request-body",
                                    List.of(error("PARAMETER", "password", "Password cannot be empty"))),
                            "POST /mvc-extended-problem-detail/handler-method-validation-exception-request-body with application/json body {\"name\":\"abc\"}");
            case "handlerMethodValidationExceptionRequestBodyValidationResult" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result",
                                    List.of(error("PARAMETER", null, "Element cannot contain empty values"))),
                            "POST /mvc-extended-problem-detail/handler-method-validation-exception-request-body-validation-result with application/json body [\"\",\"a\"]");
            case "handlerMethodValidationExceptionRequestHeader" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-request-header",
                                    List.of(error("HEADER", "headerValue", "Minimum length is 2"))),
                            "GET /mvc-extended-problem-detail/handler-method-validation-exception-request-header with header headerValue=a");
            case "handlerMethodValidationExceptionRequestParam" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-request-param",
                                    List.of(
                                            error("PARAMETER", "param", "Parameter cannot be empty"),
                                            error("PARAMETER", "param2", "Parameter 2 cannot be null"),
                                            error("PARAMETER", "param2", "Parameter 2 cannot be blank"))),
                            "GET /mvc-extended-problem-detail/handler-method-validation-exception-request-param without param and param2");
            case "handlerMethodValidationExceptionRequestPart" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-request-part",
                                    List.of(error("PARAMETER", "file", "File cannot be empty"))),
                            "GET /mvc-extended-problem-detail/handler-method-validation-exception-request-part with Content-Type: multipart/form-data and no file");
            case "handlerMethodValidationExceptionOther" ->
                    new Boot3MvcErrorResponseSpec("400", "400 validation error",
                            validationFailureExample("/mvc-extended-problem-detail/handler-method-validation-exception-other", null),
                            "GET /mvc-extended-problem-detail/handler-method-validation-exception-other");
            case "methodValidationException" ->
                    new Boot3MvcErrorResponseSpec("500", "500 validation error",
                            problemExample("Validation failed", "Internal Server Error", 500, "Validation failed",
                                    "/mvc-extended-problem-detail/method-validation-exception",
                                    List.of(
                                            error("PARAMETER", "name", "name must not be blank"),
                                            error("PARAMETER", "name", "name must not be null"),
                                            error("PARAMETER", "password", "Password and confirm password do not match"),
                                            error("PARAMETER", "name", "Name cannot be blank"),
                                            error("PARAMETER", "age", "Age cannot be null"),
                                            error("PARAMETER", "confirmPassword", "Password and confirm password do not match"),
                                            error("PARAMETER", "name", "Name length must be between 6-10"),
                                            error("PARAMETER", null, "Name is not valid"))),
                            "GET /mvc-extended-problem-detail/method-validation-exception");
            default -> new Boot3MvcErrorResponseSpec("400", "400 validation error", validationProblemDetailExample(),
                    "See the referenced MVC test for the exact validation trigger");
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
        payload.put("type", "about:blank");
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

    private static String testPath(String operationId) {
        return switch (operationId) {
            case "asyncRequestNotUsableException", "maxUploadSizeExceededException",
                 "invalidApiVersionException", "missingApiVersionException" ->
                    "src/test/java/io/github/sbracely/extended/problem/detail/webmvc/example/controller/Boot3MvcControllerRandomPortTests.java";
            default ->
                    "src/test/java/io/github/sbracely/extended/problem/detail/webmvc/example/controller/Boot3MvcControllerTests.java";
        };
    }

    private static String testGuidance(String trigger, String testPath) {
        return "Real trigger from tests: " + trigger + ". "
                + "For the exact request setup and assertions, see " + testPath + ".";
    }

    private record Boot3MvcErrorResponseSpec(String statusCode, String description, Example example, String trigger) {
    }
}
