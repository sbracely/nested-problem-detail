package io.github.sbracely.extended.problem.detail.webflux.example.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
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
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.List;

/**
 * OpenAPI configuration for the Spring WebFlux Extended Problem Detail example application.
 * <p>
 * Controller-backed examples are declared on {@code FluxExtendedProblemDetailController}. This
 * class only supplies shared schema metadata and supplemental operations that are not generated from
 * controller methods.
 */
@Configuration
public class FluxOpenApiConfiguration {

    private static final String SUPPLEMENTAL_OPERATIONS_TAG = "Supplemental Operations";

    @Bean
    OpenAPI webFluxExampleOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Extended Problem Detail Boot 4 WebFlux Example API")
                        .description("Example Spring Boot 4 WebFlux endpoints that demonstrate Extended Problem Detail responses.")
                        .version("1.1.0"))
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag()
                        .name(SUPPLEMENTAL_OPERATIONS_TAG)
                        .description("Documented operations added outside FluxExtendedProblemDetailController, including framework fallback and other supplemental endpoints."))
                .servers(List.of(new Server()
                        .url("/")
                        .description("Relative server URL")))
                .components(new Components()
                        .addSchemas("ExtendedProblemDetail", extendedProblemDetailSchema()));
    }

    @Bean
    OpenApiCustomizer webFluxErrorResponseCustomizer() {
        return FluxOpenApiConfiguration::registerSupplementalOperations;
    }

    private static void registerSupplementalOperations(OpenAPI openApi) {
        registerNoResourceFoundOperation(openApi);
        registerNotAcceptableApiVersionOperation(openApi);
    }

    private static void registerNoResourceFoundOperation(OpenAPI openApi) {
        ProblemDetail noResourceFoundExample = ProblemDetail.forStatus(404);
        noResourceFoundExample.setTitle("Not Found");
        noResourceFoundExample.setDetail("No static resource flux-extended-problem-detail/no-resource-found.");
        noResourceFoundExample.setInstance(URI.create("/flux-extended-problem-detail/no-resource-found"));
        addSupplementalGetOperation(openApi,
                "/flux-extended-problem-detail/no-resource-found",
                "noResourceFoundException",
                "404",
                "404 no resource found error",
                "Matches the documented example with the default WebFlux static-resource "
                        + "fallback handling. If resource handling is customized or disabled, "
                        + "the same request can produce a different fallback response shape.",
                new Example().summary("No resource found").value(noResourceFoundExample));
    }

    private static void registerNotAcceptableApiVersionOperation(OpenAPI openApi) {
        ProblemDetail notAcceptableApiVersionExample = ProblemDetail.forStatus(400);
        notAcceptableApiVersionExample.setTitle("Bad Request");
        notAcceptableApiVersionExample.setDetail("Invalid API version: '2.0.0'.");
        notAcceptableApiVersionExample.setInstance(URI.create("/not-acceptable-api-version"));
        addSupplementalGetOperation(openApi,
                "/not-acceptable-api-version",
                "notAcceptableApiVersionException",
                "400",
                "400 not acceptable API version error",
                "Matches the documented example when API version negotiation is enabled for the "
                        + "`API-Version` header (for example, "
                        + "`spring.webflux.apiversion.use.header=API-Version` and "
                        + "`spring.webflux.apiversion.supported=1,2`). The example application registers "
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
        if (openApi.getPaths() == null) {
            return;
        }
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
}
