package io.github.sbracely.extended.problem.detail.webflux.example.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.accept.NotAcceptableApiVersionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Supplemental WebFlux endpoint used to trigger {@link NotAcceptableApiVersionException} when API
 * version negotiation is enabled.
 */
@RestController
@ConditionalOnProperty(prefix = "spring.webflux.apiversion.use", name = "header")
public class FluxApiVersionController {

    @GetMapping(path = "/not-acceptable-api-version", version = "1")
    public Mono<Void> notAcceptableApiVersion() {
        return Mono.empty();
    }
}
