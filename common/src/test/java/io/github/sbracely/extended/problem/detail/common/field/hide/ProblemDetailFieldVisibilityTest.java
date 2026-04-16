package io.github.sbracely.extended.problem.detail.common.field.hide;

import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProblemDetailFieldVisibilityTest {

    @Test
    void shouldAllowAllFieldsWhenNoRulesAreConfigured() {
        ProblemDetailFieldVisibility visibility = ProblemDetailFieldVisibility.from(
                new ExtendedProblemDetailProperties.FieldVisibility(),
                List.of("dev"));

        assertThat(visibility.isVisible("title")).isTrue();
        assertThat(visibility.isVisible("status")).isTrue();
        assertThat(visibility.isVisible("errors")).isTrue();
        assertThat(visibility.isErrorFieldVisible("target")).isTrue();
    }

    @Test
    void shouldHideConfiguredFieldsWhenHideIsConfigured() {
        ExtendedProblemDetailProperties.FieldVisibility fieldVisibility =
                new ExtendedProblemDetailProperties.FieldVisibility();
        fieldVisibility.getHide().add("instance");
        fieldVisibility.getHide().add("errors");
        fieldVisibility.getHide().add("errors.target");

        ProblemDetailFieldVisibility visibility = ProblemDetailFieldVisibility.from(fieldVisibility, List.of());

        assertThat(visibility.isVisible("title")).isTrue();
        assertThat(visibility.isVisible("status")).isTrue();
        assertThat(visibility.isVisible("instance")).isFalse();
        assertThat(visibility.isVisible("errors")).isFalse();
        assertThat(visibility.isErrorFieldVisible("target")).isFalse();
        assertThat(visibility.isTitleVisible()).isTrue();
        assertThat(visibility.isStatusVisible()).isTrue();
        assertThat(visibility.isInstanceVisible()).isFalse();
        assertThat(visibility.isErrorsVisible()).isFalse();
        assertThat(visibility.getHiddenErrorFields()).containsExactly("target");
    }

    @Test
    void shouldUseMatchingProfileHidesInsteadOfGlobalDefaults() {
        ExtendedProblemDetailProperties.FieldVisibility fieldVisibility =
                new ExtendedProblemDetailProperties.FieldVisibility();
        fieldVisibility.getHide().add("instance");
        fieldVisibility.getHide().add("errors.target");

        ExtendedProblemDetailProperties.FieldRule dev = new ExtendedProblemDetailProperties.FieldRule();
        dev.getHide().add("status");
        dev.getHide().add("errors.message");
        fieldVisibility.getProfiles().put("dev", dev);

        ExtendedProblemDetailProperties.FieldRule prod = new ExtendedProblemDetailProperties.FieldRule();
        prod.getHide().add("detail");
        fieldVisibility.getProfiles().put("prod", prod);

        ProblemDetailFieldVisibility visibility = ProblemDetailFieldVisibility.from(
                fieldVisibility,
                List.of("dev", "prod", "qa"));

        assertThat(visibility.getHidden()).containsExactlyInAnyOrder("status", "detail");
        assertThat(visibility.isVisible("type")).isTrue();
        assertThat(visibility.isVisible("title")).isTrue();
        assertThat(visibility.isVisible("status")).isFalse();
        assertThat(visibility.isVisible("detail")).isFalse();
        assertThat(visibility.isVisible("instance")).isTrue();
        assertThat(visibility.isErrorFieldVisible("target")).isTrue();
        assertThat(visibility.isErrorFieldVisible("message")).isFalse();
        assertThat(visibility.isTypeVisible()).isTrue();
        assertThat(visibility.isTitleVisible()).isTrue();
        assertThat(visibility.isStatusVisible()).isFalse();
        assertThat(visibility.isDetailVisible()).isFalse();
        assertThat(visibility.isInstanceVisible()).isTrue();
        assertThat(visibility.getHiddenErrorFields()).containsExactly("message");
    }

    @Test
    void shouldFallbackToGlobalDefaultsWhenNoProfileMatches() {
        ExtendedProblemDetailProperties.FieldVisibility fieldVisibility =
                new ExtendedProblemDetailProperties.FieldVisibility();
        fieldVisibility.getHide().add("instance");
        fieldVisibility.getHide().add("errors.target");

        ExtendedProblemDetailProperties.FieldRule dev = new ExtendedProblemDetailProperties.FieldRule();
        dev.getHide().add("errors");
        dev.getHide().add("errors.message");
        fieldVisibility.getProfiles().put("dev", dev);

        ProblemDetailFieldVisibility visibility = ProblemDetailFieldVisibility.from(fieldVisibility, List.of("prod"));

        assertThat(visibility.getHidden()).containsExactlyInAnyOrder("instance");
        assertThat(visibility.isVisible("status")).isTrue();
        assertThat(visibility.isVisible("errors")).isTrue();
        assertThat(visibility.isVisible("instance")).isFalse();
        assertThat(visibility.isErrorFieldVisible("target")).isFalse();
        assertThat(visibility.isErrorFieldVisible("message")).isTrue();
        assertThat(visibility.isStatusVisible()).isTrue();
        assertThat(visibility.isErrorsVisible()).isTrue();
        assertThat(visibility.isInstanceVisible()).isFalse();
        assertThat(visibility.getHiddenErrorFields()).containsExactly("target");
    }
}
