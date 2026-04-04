package io.github.sbracely.extended.problem.detail.mvc.error.resolver;

import io.github.sbracely.extended.problem.detail.core.error.resolver.AbstractErrorResolver;
import io.github.sbracely.extended.problem.detail.core.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.core.response.Error;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

/**
 * Error resolver for Spring WebMVC applications.
 * <p>
 * This class extends {@link AbstractErrorResolver} to provide
 * WebMVC-specific error resolving, including support for
 * {@link MethodArgumentNotValidException} which is specific to WebMVC.
 * </p>
 * <p>
 * To customize error resolving, extend this class and override the desired methods:
 * </p>
 * <ul>
 *     <li>{@link #resolveMethodArgumentNotValidException(MethodArgumentNotValidException)} - for @Valid argument errors</li>
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
public class MvcErrorResolver extends AbstractErrorResolver {

    /**
     * Constructs a new resolver with the specified log instance.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     */
    public MvcErrorResolver(ExtendedProblemDetailLog extendedProblemDetailLog) {
        super(extendedProblemDetailLog);
    }

    /**
     * Resolves errors from {@link MethodArgumentNotValidException}.
     * <p>
     * This method is specific to WebMVC and resolves errors for
     * {@code @Valid} annotated method arguments.
     * </p>
     *
     * @param ex the MethodArgumentNotValidException to resolve
     * @return list of Error objects representing all errors
     */
    public List<Error> resolveMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return resolveBindingResult(ex.getBindingResult());
    }
}
