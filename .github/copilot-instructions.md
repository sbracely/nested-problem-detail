# Copilot instructions for `extended-problem-detail`

## Build, test, and lint commands

- CI build: `.\mvnw.cmd -B verify` on Windows, or `./mvnw -B verify` in GitHub Actions.
- Full clean build: `.\mvnw.cmd clean verify`
- Full test suite: `.\mvnw.cmd test`
- Single common response test class: `.\mvnw.cmd -pl common -Dtest=ErrorTest test`
- Single MVC auto-config test class: `.\mvnw.cmd -pl webmvc\autoconfigure -Dtest=MvcExtendedProblemDetailAutoConfigurationTests test`
- Single WebFlux auto-config test class: `.\mvnw.cmd -pl webflux\autoconfigure -Dtest=FluxExtendedProblemDetailAutoConfigurationTests test`
- Single MVC example controller test class: `.\mvnw.cmd -pl webmvc\example -Dtest=MvcControllerTests test`
- Single WebFlux example contract test class: `.\mvnw.cmd -pl webflux\example -Dtest=FluxOpenApiDefaultContractTests test`
- Generate offline OpenAPI docs: `.\mvnw.cmd -pl webmvc\example -Poffline-openapi-docs verify` and `.\mvnw.cmd -pl webflux\example -Poffline-openapi-docs verify`

## High-level architecture

- This is a Maven reactor with three top-level modules: `common`, `webmvc`, and `webflux`.
- `common` is the shared support layer for both web stacks. It contains the shared error entry model (`Error`), shared validation-to-error mapping (`ExtendedProblemDetailErrorResolver`), message-code resolution (`ExtendedProblemDetailMessageResolver`), shared property model (`ExtendedProblemDetailProperties`), and logging/startup-notice support (`ExtendedProblemDetailLog`, `ExtendedProblemDetailStartupLogger`).
- `webmvc` and `webflux` each split into three submodules:
  - `autoconfigure`: the real runtime integration layer
  - `starter`: a thin published starter that depends on the corresponding autoconfigure module
  - `example`: a runnable sample application that also provides end-to-end HTTP and OpenAPI contract coverage
- The actual framework-specific behavior lives in the exception handlers and auto-configurations, not the starters:
  - `webmvc\autoconfigure\...\MvcExtendedProblemDetailAutoConfiguration`
  - `webmvc\autoconfigure\...\MvcExtendedProblemDetailExceptionHandler`
  - `webflux\autoconfigure\...\FluxExtendedProblemDetailAutoConfiguration`
  - `webflux\autoconfigure\...\FluxExtendedProblemDetailExceptionHandler`
- Both auto-configuration modules register the same kinds of infrastructure: the stack-specific exception handler, shared logging support, and a startup logger that emits a one-time reminder when `extended.problem-detail.enabled` is left unset.
- Both framework-specific handlers start from Spring's own `ProblemDetail` body, resolve structured `errors`, and add them as a `ProblemDetail` properties entry with key `errors`. Business exceptions in the example apps also use the shared message resolver so localized message codes can flow into the final payload.
- Example modules are part of the reactor on purpose: they are the executable demos, the source of full-stack HTTP assertions, and the source of the generated offline OpenAPI documents under each module's `docs` directory. Some MVC OpenAPI operations are synthetic so documented framework fallback exceptions can still be contract-tested.
- Boot auto-configuration registration is resource-based. If an auto-configuration class is renamed or moved, update:
  - `webmvc\autoconfigure\src\main\resources\META-INF\spring\org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  - `webflux\autoconfigure\src\main\resources\META-INF\spring\org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- The root, `webmvc`, and `webflux` POMs are reactor aggregators only. Published artifacts are `common`, both `autoconfigure` modules, and both `starter` modules.

## Key conventions

- Put shared error-shaping changes in `common\...\ExtendedProblemDetailErrorResolver` first. MVC and WebFlux handlers are expected to stay thin adapters unless behavior is truly stack-specific.
- The main customization seam is subclassing the stack-specific exception handler and overriding either a `handle...` method in the handler or a `resolveXxx(...)` method from `ExtendedProblemDetailErrorResolver`.
- Default beans are registered with `@ConditionalOnMissingBean`. A custom handler bean is meant to replace the auto-configured one; avoid introducing parallel competing handlers unless that is intentional.
- The configuration prefix is `extended.problem-detail`. Default behavior is enabled, logs at `INFO`, and omits stack traces. If `extended.problem-detail.enabled` is omitted entirely, the startup logger emits a one-time reminder; explicitly setting it to `true` or `false` suppresses that message.
- Message text can be locale-sensitive. Tests that assert concrete messages or compare against generated OpenAPI docs pin English explicitly with `Locale.ENGLISH` or `Accept-Language` headers so docs and runtime responses stay stable.
- Tests are split by purpose:
  - autoconfigure modules use `WebApplicationContextRunner` / `ReactiveWebApplicationContextRunner`
  - handler tests exercise framework adapters directly
  - MVC example tests use `MockMvcTester`, and real-HTTP MVC cases use `@AutoConfigureRestTestClient` with `RestTestClient`
  - WebFlux example tests use `WebTestClient`
  - OpenAPI contract tests compare live responses against `/v3/api-docs`, so response-shape changes usually require fixture and contract-test updates together
- In API-version contract scenarios, fetch `/v3/api-docs` with the `API-Version` header so the generated docs match the versioned behavior under test.
- When changing public error behavior, update both stacks unless the difference is intentionally framework-specific. The example modules and handler tests are the best place to confirm parity.
- Release/version bumps are explicit: the published modules inherit `spring-boot-starter-parent`, not the root aggregator, so version updates must touch each module POM directly plus the `webmvc` / `webflux` aggregator parent references. Keep published POM `scm.tag` aligned with `v${project.version}`.
