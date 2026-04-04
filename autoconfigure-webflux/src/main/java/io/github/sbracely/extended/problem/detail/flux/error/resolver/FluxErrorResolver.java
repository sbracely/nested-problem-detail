package io.github.sbracely.extended.problem.detail.flux.error.resolver;

import io.github.sbracely.extended.problem.detail.core.error.resolver.AbstractErrorResolver;
import io.github.sbracely.extended.problem.detail.core.logging.ExtendedProblemDetailLog;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * Error resolver for Spring WebFlux applications.
 * <p>
 * This class extends {@link AbstractErrorResolver} to provide
 * WebFlux-specific error resolving. In WebFlux, errors are typically
 * represented by {@link WebExchangeBindException} which is already
 * handled by the base class.
 * </p>
 * <p>
 * To customize error resolving, extend this class and override the desired methods:
 * </p>
 * <ul>
 *     <li>{@link #resolveWebExchangeBindException(WebExchangeBindException)} - for data binding errors</li>
 *     <li>{@link #resolveCookieValue} - for @CookieValue errors</li>
 *     <li>{@link #resolvePathVariable} - for @PathVariable errors</li>
 *     <li>{@link #resolveRequestBody} - for @RequestBody errors</li>
 *     <li>{@link #resolveRequestHeader} - for @RequestHeader errors</li>
 *     <li>{@link #resolveRequestParam} - for @RequestParam errors</li>
 *     <li>and other resolveXxx methods</li>
 * </ul>
 *
 * @see AbstractErrorResolver
 * @since 1.0.0
 */
public class FluxErrorResolver extends AbstractErrorResolver {

    /**
     * Constructs a new resolver with the specified log instance.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     */
    public FluxErrorResolver(ExtendedProblemDetailLog extendedProblemDetailLog) {
        super(extendedProblemDetailLog);
    }
}
