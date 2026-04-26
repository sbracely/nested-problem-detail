package io.github.sbracely.extended.problem.detail.webmvc.example.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.accept.NotAcceptableApiVersionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Supplemental MVC endpoint used to trigger {@link NotAcceptableApiVersionException} when API
 * version negotiation is enabled.
 */
@RestController
@ConditionalOnProperty(prefix = "spring.mvc.apiversion.use", name = "header")
public class MvcApiVersionController {

    @GetMapping(path = "/not-acceptable-api-version", version = "1")
    public void notAcceptableApiVersion() {
    }
}
