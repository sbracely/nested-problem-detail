package io.github.sbracely.extended.problem.detail.common.condition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FieldHideConfiguredConditionTest {

    private final FieldHideConfiguredCondition condition = new FieldHideConfiguredCondition();
    private static final AnnotationMetadata TEST_METADATA =
            AnnotationMetadata.introspect(FieldHideConfiguredConditionTest.class);

    @Test
    void shouldMatchWhenAnyActiveProfileConfiguresHide() {
        ConfigurableEnvironment environment = environment(
                Map.of("extended.problem-detail.field.profiles.dev.hide[0]", "status"),
                "dev",
                "prod");

        assertThat(condition.matches(new TestConditionContext(environment), TEST_METADATA)).isTrue();
    }

    @Test
    void shouldMatchWhenGlobalHideConfiguredAndNoProfileMatches() {
        ConfigurableEnvironment environment = environment(
                Map.of("extended.problem-detail.field.hide[0]", "detail"),
                "dev");

        assertThat(condition.matches(new TestConditionContext(environment), TEST_METADATA)).isTrue();
    }

    @Test
    void shouldNotFallbackToGlobalHideWhenMatchedProfileHasNoConfiguredHide() {
        ConfigurableEnvironment environment = environment(
                Map.of(
                        "extended.problem-detail.field.hide[0]", "detail",
                        "extended.problem-detail.field.profiles.dev.hide[0]", " "),
                "dev");

        assertThat(condition.matches(new TestConditionContext(environment), TEST_METADATA)).isFalse();
    }

    private static ConfigurableEnvironment environment(Map<String, Object> properties, String... activeProfiles) {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", properties));
        environment.setActiveProfiles(activeProfiles);
        return environment;
    }

    private record TestConditionContext(Environment environment) implements ConditionContext {

        @Override
        public BeanDefinitionRegistry getRegistry() {
            return null;
        }

        @Override
        public ConfigurableListableBeanFactory getBeanFactory() {
            return null;
        }

        @Override
        public Environment getEnvironment() {
            return environment;
        }

        @Override
        public ResourceLoader getResourceLoader() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }
    }
}
