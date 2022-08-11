package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.domain.RoutingPatternElement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class NamedPatternTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "A", "1", ".", "foo-bar", "foo_bar", "a_A-1."})
    void can_create_pattern(String name) {
        assertThat(NamedPattern.parseOrNull(new RoutingPatternElement("{%s}".formatted(name))))
                .describedAs("Pattern %s".formatted(name)).isNotNull();
    }
}