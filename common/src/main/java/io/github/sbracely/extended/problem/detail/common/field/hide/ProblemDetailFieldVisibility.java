package io.github.sbracely.extended.problem.detail.common.field.hide;

import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Effective field visibility rules for ProblemDetail serialization.
 *
 * @since 1.1.0
 */
public final class ProblemDetailFieldVisibility {

    private static final String ERRORS_PREFIX = "errors.";

    private final Set<String> hidden;
    private final Set<String> hiddenErrorFields;
    private final boolean typeVisible;
    private final boolean titleVisible;
    private final boolean statusVisible;
    private final boolean detailVisible;
    private final boolean instanceVisible;
    private final boolean errorsVisible;

    private ProblemDetailFieldVisibility(Set<String> hidden, Set<String> hiddenErrorFields) {
        this.hidden = Set.copyOf(hidden);
        this.hiddenErrorFields = Set.copyOf(hiddenErrorFields);
        this.typeVisible = computeVisibility("type");
        this.titleVisible = computeVisibility("title");
        this.statusVisible = computeVisibility("status");
        this.detailVisible = computeVisibility("detail");
        this.instanceVisible = computeVisibility("instance");
        this.errorsVisible = computeVisibility("errors");
    }

    /**
     * Creates effective field visibility from global and profile-specific rules.
     *
     * @param fieldVisibility the configured field visibility
     * @param activeProfiles  the active Spring profiles
     * @return the effective field visibility
     */
    public static ProblemDetailFieldVisibility from(
            ExtendedProblemDetailProperties.FieldVisibility fieldVisibility,
            Collection<String> activeProfiles) {
        HiddenFields hiddenFields = new HiddenFields();
        Map<String, ExtendedProblemDetailProperties.FieldRule> profiles = fieldVisibility.getProfiles();
        boolean matchedProfile = false;
        if (profiles != null) {
            for (String activeProfile : activeProfiles) {
                ExtendedProblemDetailProperties.FieldRule rule = profiles.get(activeProfile);
                if (rule == null) {
                    continue;
                }
                matchedProfile = true;
                hiddenFields.add(normalized(rule.getHide()));
            }
        }
        if (!matchedProfile) {
            hiddenFields.add(normalized(fieldVisibility.getHide()));
        }
        return new ProblemDetailFieldVisibility(hiddenFields.problemDetailFields, hiddenFields.errorFields);
    }

    /**
     * Creates a visibility instance with no filtering.
     *
     * @return the default visibility
     */
    public static ProblemDetailFieldVisibility allowAll() {
        return new ProblemDetailFieldVisibility(Set.of(), Set.of());
    }

    /**
     * Whether the given field should be serialized.
     *
     * @param fieldName the JSON field name
     * @return {@code true} when the field should be serialized
     */
    public boolean isVisible(String fieldName) {
        return !hidden.contains(fieldName);
    }

    /**
     * Whether the given {@code errors[]} field should be serialized.
     *
     * @param fieldName the error JSON field name
     * @return {@code true} when the field should be serialized
     */
    public boolean isErrorFieldVisible(String fieldName) {
        return !hiddenErrorFields.contains(fieldName);
    }

    /**
     * Whether the standard {@code type} field should be serialized.
     *
     * @return {@code true} when the field should be serialized
     */
    public boolean isTypeVisible() {
        return typeVisible;
    }

    /**
     * Whether the standard {@code title} field should be serialized.
     *
     * @return {@code true} when the field should be serialized
     */
    public boolean isTitleVisible() {
        return titleVisible;
    }

    /**
     * Whether the standard {@code status} field should be serialized.
     *
     * @return {@code true} when the field should be serialized
     */
    public boolean isStatusVisible() {
        return statusVisible;
    }

    /**
     * Whether the standard {@code detail} field should be serialized.
     *
     * @return {@code true} when the field should be serialized
     */
    public boolean isDetailVisible() {
        return detailVisible;
    }

    /**
     * Whether the standard {@code instance} field should be serialized.
     *
     * @return {@code true} when the field should be serialized
     */
    public boolean isInstanceVisible() {
        return instanceVisible;
    }

    /**
     * Whether the {@code errors} field should be serialized.
     *
     * @return {@code true} when the field should be serialized
     */
    public boolean isErrorsVisible() {
        return errorsVisible;
    }

    Set<String> getHidden() {
        return hidden;
    }

    Set<String> getHiddenErrorFields() {
        return hiddenErrorFields;
    }

    private static HiddenFields normalized(Collection<String> fields) {
        HiddenFields normalized = new HiddenFields();
        if (fields == null) {
            return normalized;
        }
        for (String field : fields) {
            if (field == null) {
                continue;
            }
            String candidate = field.trim();
            if (!candidate.isEmpty()) {
                if (candidate.startsWith(ERRORS_PREFIX)) {
                    String errorField = candidate.substring(ERRORS_PREFIX.length()).trim();
                    if (!errorField.isEmpty()) {
                        normalized.errorFields.add(errorField);
                    }
                    continue;
                }
                normalized.problemDetailFields.add(candidate);
            }
        }
        return normalized;
    }

    private boolean computeVisibility(String fieldName) {
        return !hidden.contains(fieldName);
    }

    private static final class HiddenFields {

        private final Set<String> problemDetailFields = new LinkedHashSet<>();
        private final Set<String> errorFields = new LinkedHashSet<>();

        private void add(HiddenFields hiddenFields) {
            problemDetailFields.addAll(hiddenFields.problemDetailFields);
            errorFields.addAll(hiddenFields.errorFields);
        }
    }
}
