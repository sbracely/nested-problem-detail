package io.github.sbracely.extended.problem.detail.common.condition;

import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Matches when extended problem detail logging is enabled.
 *
 * @since 1.1.0
 */
public final class LoggingEnabledCondition implements Condition {

    /**
     * Creates a new condition instance.
     */
    public LoggingEnabledCondition() {
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        LogLevel level = Binder.get(context.getEnvironment())
                .bind("extended.problem-detail.logging.at-level", LogLevel.class)
                .orElseGet(() -> new ExtendedProblemDetailProperties.CommonLogging().getAtLevel());
        return level != LogLevel.OFF;
    }
}
