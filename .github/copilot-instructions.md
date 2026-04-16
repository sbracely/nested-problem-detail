# Copilot instructions for `extended-problem-detail`

## Build, test, and lint commands

- Full build: `.\mvnw.cmd clean verify`
- Full test suite: `.\mvnw.cmd test`
- Single test class: `.\mvnw.cmd -pl common -Dtest=ExtendedProblemDetailTest test`
- Another single-test example: `.\mvnw.cmd -pl webmvc\autoconfigure -Dtest=MvcExtendedProblemDetailAutoConfigurationTests test`
- WebFlux single-test example: `.\mvnw.cmd -pl webflux\example -Dtest=FluxOpenApiDefaultContractTests test`
- Generate offline OpenAPI docs: `.\mvnw.cmd -pl webmvc\example -Poffline-openapi-docs verify` and `.\mvnw.cmd -pl webflux\example -Poffline-openapi-docs verify`
- Static analysis: `qodana scan` (configuration lives in `qodana.yaml`)

## High-level architecture

- This is a Maven reactor with three top-level modules: `common`, `webmvc`, and `webflux`.
- `common` contains the shared model and behavior used by both stacks:
  - `ExtendedProblemDetail` extends Spring's `ProblemDetail` with an `errors` list.
  - `Error` is the shared error record returned in API responses.
  - `ExtendedProblemDetailErrorResolver` contains the shared conversion logic from Spring validation exceptions to `Error` entries.
  - `ExtendedProblemDetailLog` centralizes configurable logging behavior.
  - `ExtendedProblemDetailProperties` defines the shared `extended.problem-detail.*` property model.
  - `field.hide` support also lives here: shared field-visibility rules and Jackson serializers are in `common`, then wired by each web stack.
- `webmvc` and `webflux` each split into three submodules:
  - `autoconfigure`: the real runtime integration layer
  - `starter`: a thin published starter that depends on the corresponding autoconfigure module
  - `example`: a runnable demo/test application that is not meant to be deployed to Maven Central
- The published starters are intentionally thin. The actual framework behavior lives in:
  - `webmvc\autoconfigure\...\MvcExtendedProblemDetailAutoConfiguration`
  - `webmvc\autoconfigure\...\MvcExtendedProblemDetailExceptionHandler`
  - `webflux\autoconfigure\...\FluxExtendedProblemDetailAutoConfiguration`
  - `webflux\autoconfigure\...\FluxExtendedProblemDetailExceptionHandler`
- Both auto-configuration modules register the same kinds of infrastructure: optional logging, optional field-visibility/Jackson serialization, and the framework-specific exception handler.
- Both framework-specific exception handlers build on Spring's own `ProblemDetail` body and then wrap it with `ExtendedProblemDetail.from(...)` after resolving detailed errors.
- The example modules are more than demos: they also hold the end-to-end HTTP coverage and the OpenAPI contract surface. Their OpenAPI configs rewrite generated docs so the documented `application/problem+json` examples match the actual error responses.
- Some example OpenAPI operations are synthetic rather than controller-backed, especially on MVC, so framework fallback exceptions can still be documented and contract-tested.
- Boot auto-configuration registration is resource-based. If an auto-configuration class is renamed or moved, update:
  - `webmvc\autoconfigure\src\main\resources\META-INF\spring\org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  - `webflux\autoconfigure\src\main\resources\META-INF\spring\org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## Key conventions

- Put shared validation-to-error mapping changes in `common\...\ExtendedProblemDetailErrorResolver` first. MVC and WebFlux handlers should stay as thin framework adapters unless the behavior is truly stack-specific.
- The customization seam is subclassing the framework-specific exception handler and overriding either:
  - a `handle...` method in the handler class, or
  - a `resolveXxx(...)` method from `ExtendedProblemDetailErrorResolver`
- Default beans are registered with `@ConditionalOnMissingBean`. A custom handler bean replaces the auto-configured one; do not add parallel competing handlers unless that is intentional.
- The configuration prefix is `extended.problem-detail`. The default behavior is enabled, logs at `INFO`, and does not print stack traces.
- `extended.problem-detail.field.hide` is not just documentation; it activates custom Jackson serializers. If one or more configured profile rules match the active Spring profiles, those profile hide rules replace the default hide set, and multiple matching profiles are unioned together.
- Example modules are part of the reactor because they provide end-to-end coverage and usage examples, but they are explicitly non-deployable (`maven.deploy.skip=true`).
- Tests are split by purpose:
  - autoconfigure modules use `WebApplicationContextRunner` / `ReactiveWebApplicationContextRunner` to verify bean registration and property binding
  - handler tests cover framework adapters directly
  - example modules use `MockMvcTester` or `WebTestClient` to verify full HTTP `application/problem+json` responses and concrete `errors` payloads
  - example OpenAPI contract tests compare live responses against `/v3/api-docs`, so doc changes usually require matching fixture and contract-test updates
- For MVC tests that need real HTTP instead of MockMvc, use `@AutoConfigureRestTestClient` with `RestTestClient`. In the API-version scenario, fetch `/v3/api-docs` with the `API-Version` header so the docs match that configuration.
- When changing public error-shaping behavior, update both stacks unless the change is intentionally framework-specific. The examples and handler tests are the best place to confirm parity.
