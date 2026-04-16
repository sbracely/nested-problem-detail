package io.github.sbracely.extended.problem.detail.common.condition;

import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Matches when effective field hide rules are configured for the current active profiles.
 *
 * @since 1.1.0
 */
public final class FieldHideConfiguredCondition implements Condition {

    /**
     * Creates a new condition instance.
     */
    public FieldHideConfiguredCondition() {
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ExtendedProblemDetailProperties.FieldVisibility fieldVisibility = Binder.get(context.getEnvironment())
                .bind("extended.problem-detail.field", Bindable.of(ExtendedProblemDetailProperties.FieldVisibility.class))
                .orElseGet(ExtendedProblemDetailProperties.FieldVisibility::new);
        return hasConfiguredHide(fieldVisibility, List.of(context.getEnvironment().getActiveProfiles()));
    }

    private static boolean hasConfiguredHide(
            ExtendedProblemDetailProperties.FieldVisibility fieldVisibility,
            Collection<String> activeProfiles) {
        Map<String, ExtendedProblemDetailProperties.FieldRule> profiles = fieldVisibility.getProfiles();
        boolean matchedProfile = false;
        if (profiles != null) {
            for (String activeProfile : activeProfiles) {
                ExtendedProblemDetailProperties.FieldRule rule = profiles.get(activeProfile);
                if (rule == null) {
                    continue;
                }
                matchedProfile = true;
                if (hasConfiguredHide(rule.getHide())) {
                    return true;
                }
            }
        }
        return !matchedProfile && hasConfiguredHide(fieldVisibility.getHide());
    }

    private static boolean hasConfiguredHide(Collection<String> configuredFields) {
        if (configuredFields == null) {
            return false;
        }
        for (String configuredField : configuredFields) {
            if (configuredField == null) {
                continue;
            }
            String candidate = configuredField.trim();
            if (!candidate.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
