package io.github.sbracely.extended.problem.detail.webmvc.example.config;

import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

/**
 * OpenAPI configuration for the Spring WebMVC Extended Problem Detail example application.
 * <p>
 * This configuration class registers two Spring beans:
 * </p>
 * <ul>
 *     <li>{@link #webMvcExampleOpenApi()} – defines the top-level API metadata, server URL, reusable
 *         component schemas ({@code Error} and {@code ExtendedProblemDetail}), and a set of shared
 *         {@code application/problem+json} examples that represent each supported error scenario.</li>
 *     <li>{@link #webMvcErrorResponseCustomizer()} – an {@link OpenApiCustomizer} that rewrites the
 *         auto-generated operation responses so that every endpoint shows the concrete problem-detail
 *         response it produces instead of the generic 200/default entries created by SpringDoc.</li>
 * </ul>
 */
@Configuration
public class MvcOpenApiConfiguration {

    private static final String SUPPLEMENTAL_OPERATIONS_TAG = "Supplemental Operations";

    /**
     * Creates and registers the {@link OpenAPI} bean that describes the WebMVC example API.
     * <p>
     * The returned instance is configured with:
     * </p>
     * <ul>
     *     <li><b>Info</b> – title {@code "Extended Problem Detail WebMVC Example API"}, a short
     *         description of the application's purpose, and version {@code 1.1.0}.</li>
     *     <li><b>Servers</b> – a single relative server entry with URL {@code "/"} so that the
     *         Swagger UI "Try it out" feature works regardless of the host the application is
     *         deployed on.</li>
     *     <li><b>Components / Schemas</b> – one reusable schema definition:
     *         <ul>
     *             <li>{@code ExtendedProblemDetail} – the RFC 9457 problem-detail object augmented
     *                 with the {@code errors} array whose item schema is defined inline.</li>
     *         </ul>
     *     </li>
     *     <li><b>Components / Examples</b> – seven named examples that illustrate the
     *         {@code application/problem+json} response body for the most common error scenarios:
     *         {@code ValidationProblemDetailExample}, {@code GenericBadRequestProblemDetailExample},
     *         {@code MethodNotAllowedProblemDetailExample}, {@code NotAcceptableProblemDetailExample},
     *         {@code UnsupportedMediaTypeProblemDetailExample}, {@code ServerProblemDetailExample},
     *         and {@code BusinessProblemDetailExample}.</li>
     * </ul>
     *
     * @return an {@link OpenAPI} instance pre-populated with the example API metadata and reusable
     * component definitions
     */
    @Bean
    OpenAPI webMvcExampleOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Extended Problem Detail Boot 4 WebMVC Example API")
                        .description("Example Spring Boot 4 WebMVC endpoints that demonstrate Extended Problem Detail responses.")
                        .version("1.1.0"))
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
                        .name(SUPPLEMENTAL_OPERATIONS_TAG)
                        .description("Documented operations added outside MvcProblemDetailController, including framework fallback and other supplemental endpoints."))
                .servers(List.of(new Server()
                        .url("/")
                        .description("Relative server URL")))
                .components(new Components()
                        .addSchemas("ExtendedProblemDetail", extendedProblemDetailSchema()));
    }

    private static Schema<?> extendedProblemDetailSchema() {
        StringSchema errorType = new StringSchema();
        errorType.description("Source of the error.");
        errorType.addEnumItemObject("PARAMETER");
        errorType.addEnumItemObject("COOKIE");
        errorType.addEnumItemObject("HEADER");
        errorType.addEnumItemObject("BUSINESS");

        ObjectSchema errorItemSchema = new ObjectSchema();
        errorItemSchema.description("Detailed error entry inside the Extended Problem Detail response.");
        errorItemSchema.addProperty("type", errorType);
        errorItemSchema.addProperty("target", new StringSchema()
                .description("Field, parameter, cookie, header, or business target associated with the error."));
        errorItemSchema.addProperty("message", new StringSchema()
                .description("Human-readable explanation of the error."));

        ArraySchema errorsSchema = new ArraySchema();
        errorsSchema.description("Detailed validation or business errors.");
        errorsSchema.items(errorItemSchema);

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
                .addProperty("errors", errorsSchema);
    }

    /**
     * Creates and registers an {@link OpenApiCustomizer} bean that tailors the operation responses
     * generated by SpringDoc for every path in the example API.
     * <p>
     * For each operation the customizer:
     * </p>
     * <ol>
     *     <li>Ensures the {@code ExtendedProblemDetail} schema is present in the
     *         {@link io.swagger.v3.oas.models.Components} section of the document.</li>
     *     <li>Registers the small set of synthetic operations that do not have controller methods,
     *         together with their fallback responses.</li>
     *     <li>Adds scenario metadata used by the contract tests.</li>
     * </ol>
     *
     * @return an {@link OpenApiCustomizer} that rewrites each operation's responses to reflect the
     * actual {@code application/problem+json} error contract
     */
    @Bean
    OpenApiCustomizer webMvcErrorResponseCustomizer() {
        return MvcOpenApiConfiguration::registerSupplementalOperations;
    }

    private static void registerSupplementalOperations(OpenAPI openApi) {
        registerNoResourceFoundOperation(openApi);
        registerNoHandlerFoundOperation(openApi);
        registerInvalidEndpointBadRequestOperation(openApi);
        registerNotAcceptableApiVersionOperation(openApi);
    }

    private static void registerNoResourceFoundOperation(OpenAPI openApi) {
        ExtendedProblemDetail noResourceFoundExample = new ExtendedProblemDetail();
        noResourceFoundExample.setTitle("Not Found");
        noResourceFoundExample.setStatus(404);
        noResourceFoundExample.setDetail("No static resource mvc-extended-problem-detail/no-resource-found-exception.");
        noResourceFoundExample.setInstance(URI.create("/mvc-extended-problem-detail/no-resource-found-exception"));
        addSupplementalGetOperation(openApi,
                "/mvc-extended-problem-detail/no-resource-found-exception",
                "noResourceFoundException",
                "404",
                "404 no resource found error",
                "Matches the documented example with the default static-resource handling "
                        + "configuration (`spring.web.resources.add-mappings=true`, which is also the "
                        + "Spring Boot default). If static resource mappings are disabled, the same "
                        + "request is handled as `NoHandlerFoundException` instead.",
                new Example().summary("No resource found").value(noResourceFoundExample));
    }

    private static void registerNoHandlerFoundOperation(OpenAPI openApi) {
        ExtendedProblemDetail noHandlerFoundExample = new ExtendedProblemDetail();
        noHandlerFoundExample.setTitle("Not Found");
        noHandlerFoundExample.setStatus(404);
        noHandlerFoundExample.setDetail("No endpoint GET /mvc-extended-problem-detail/no-handler-found-exception.");
        noHandlerFoundExample.setInstance(URI.create("/mvc-extended-problem-detail/no-handler-found-exception"));
        addSupplementalGetOperation(openApi,
                "/mvc-extended-problem-detail/no-handler-found-exception",
                "noHandlerFoundException",
                "404",
                "404 no handler found error",
                "Matches the documented example when static resource mappings are disabled via "
                        + "`spring.web.resources.add-mappings=false`. Otherwise Spring treats the "
                        + "request as a static-resource lookup and returns the `NoResourceFoundException` "
                        + "shape instead.",
                new Example().summary("No handler found").value(noHandlerFoundExample));
    }

    private static void registerInvalidEndpointBadRequestOperation(OpenAPI openApi) {
        ExtendedProblemDetail invalidEndpointExample = new ExtendedProblemDetail();
        invalidEndpointExample.setTitle("Bad Request");
        invalidEndpointExample.setStatus(400);
        invalidEndpointExample.setDetail("Missing parameters: param1,param2");
        invalidEndpointExample.setInstance(URI.create("/actuator/demo/name"));
        addSupplementalGetOperation(openApi,
                "/actuator/demo/{name}",
                "invalidEndpointBadRequestException",
                "400",
                "400 invalid actuator endpoint request error",
                "Matches the documented example when the demo actuator endpoint is exposed with "
                        + "`management.endpoints.web.exposure.include=demo`. Without that exposure, "
                        + "`/actuator/demo/{name}` is not available.",
                new Example().summary("Invalid actuator endpoint request").value(invalidEndpointExample));
    }

    private static void registerNotAcceptableApiVersionOperation(OpenAPI openApi) {
        ExtendedProblemDetail notAcceptableApiVersionExample = new ExtendedProblemDetail();
        notAcceptableApiVersionExample.setTitle("Bad Request");
        notAcceptableApiVersionExample.setStatus(400);
        notAcceptableApiVersionExample.setDetail("Invalid API version: '2.0.0'.");
        notAcceptableApiVersionExample.setInstance(URI.create("/not-acceptable-api-version"));
        addSupplementalGetOperation(openApi,
                "/not-acceptable-api-version",
                "notAcceptableApiVersionException",
                "400",
                "400 not acceptable API version error",
                "Matches the documented example when API version negotiation is enabled for the "
                        + "`API-Version` header (for example, "
                        + "`spring.mvc.apiversion.use.header=API-Version` and "
                        + "`spring.mvc.apiversion.supported=1,2`). The example application registers "
                        + "a versioned `GET /not-acceptable-api-version` handler only in that setup.",
                new Example().summary("Not acceptable API version").value(notAcceptableApiVersionExample));
    }

    private static void addSupplementalGetOperation(OpenAPI openApi,
                                                    String path,
                                                    String operationId,
                                                    String statusCode,
                                                    String description,
                                                    String operationDescription,
                                                    Example example) {
        PathItem pathItem = openApi.getPaths().get(path);
        if (pathItem == null) {
            pathItem = new PathItem();
            openApi.getPaths().addPathItem(path, pathItem);
        }
        if (pathItem.getGet() == null) {
            MediaType mediaType = new MediaType();
            mediaType.schema(new Schema<>().$ref("#/components/schemas/ExtendedProblemDetail"));
            mediaType.addExamples("example", example);

            Content content = new Content();
            content.addMediaType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE, mediaType);

            ApiResponse response = new ApiResponse();
            response.description(description);
            response.content(content);

            ApiResponses responses = new ApiResponses();
            responses.addApiResponse(statusCode, response);

            Operation operation = new Operation();
            operation.operationId(operationId);
            operation.addTagsItem(SUPPLEMENTAL_OPERATIONS_TAG);
            operation.description(operationDescription);
            operation.responses(responses);
            pathItem.setGet(operation);
        }
    }


}
